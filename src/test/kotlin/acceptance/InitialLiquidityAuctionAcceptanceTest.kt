package acceptance

import helpers.VaultState
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.testcontainers.junit.jupiter.Testcontainers
import org.web3j.protocol.exceptions.TransactionException
import java.math.BigInteger
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

@Testcontainers
class InitialLiquidityAuctionAcceptanceTest : AcceptanceTest() {

    val toBuy = toTokenAmount(20)

    /**
     * @given a vault with limit not breached
     * @when close-out initiator calls startInitialLiquidityAuction()
     * @then error returned - the vault is not in breach
     */
    @Test
    fun closeOutWithoutBreach() {
        ownerCreatesVault()

        assertThrows<TransactionException> {
            vaultByInitiator.startInitialLiquidityAuction().send()
        }
    }

    /**
     * @given a vault is closed
     * @when close-out initiator calls startInitialLiquidityAuction()
     * @then error returned - the vault is closed
     */
    @Test
    fun closeOutAlreadyClosed() {
        ownerCreatesVault()
        vaultByOwner.close().send()

        assertThrows<TransactionException> {
            vaultByInitiator.startInitialLiquidityAuction().send()
        }
    }

    /**
     * @given a vault with limit breached and close out already has called
     * @when close-out initiator calls startInitialLiquidityAuction()
     * @then error returned - the vault is close-out state
     */
    @Test
    fun closeOutDoubleCalled() {
        ownerCreatesVault()
        ownerBreachesVault()
        vaultByInitiator.startInitialLiquidityAuction().send()

        assertThrows<TransactionException> {
            vaultByInitiator.startInitialLiquidityAuction().send()
        }
    }

    /**
     * @given a breached vault with closed out process initiated
     * @when getPrice called over time
     * @then the price of TKN is discounted by 1% of initial value every 30 minutes
     */
    @Test
    fun closeOutDutchAuctionPriceChange() {
        val timeInterval = BigInteger.valueOf(30 * 60) // 30 min
        val price = toTokenAmount(100000)
        ownerCreatesVault(tokenPrice = price)
        ownerBreachesVault()
        assertEquals(VaultState.Defaulted.toBigInteger(), vaultByOwner.state.send())
        vaultByInitiator.startInitialLiquidityAuction().send()
        assertEquals(VaultState.InitialLiquidityAuctionInProcess.toBigInteger(), vaultByOwner.state.send())

        assertEquals(price, vaultByOwner.price.send())

        helper.passTime(timeInterval)
        assertEquals(toTokenAmount(99000), vaultByOwner.price.send())
        assertEquals(VaultState.InitialLiquidityAuctionInProcess.toBigInteger(), vaultByOwner.state.send())

        helper.passTime(timeInterval)
        assertEquals(toTokenAmount(98000), vaultByOwner.price.send())
        assertEquals(VaultState.InitialLiquidityAuctionInProcess.toBigInteger(), vaultByOwner.state.send())

        helper.passTime(timeInterval.multiply(BigInteger.valueOf(97)))
        assertEquals(toTokenAmount(1000), vaultByOwner.price.send())
        assertEquals(VaultState.InitialLiquidityAuctionInProcess.toBigInteger(), vaultByOwner.state.send())

        helper.passTime(timeInterval)
        assertEquals(toTokenAmount(0), vaultByOwner.price.send())
        assertEquals(VaultState.WaitingForSlashing.toBigInteger(), vaultByOwner.state.send())
    }

    /**
     * @given the vault is in the breach state and Initial Liquidity Auction is not started. And the vault and buyer
     * have enough funds for the deal (the vault has at least 20 TKN (user tokens) and the buyer has 40 EAU and price is
     * 2 TKN/EAU)
     * @when the buyer buys 20 TKN with price at least 2 TKN/EAU
     * @then deal accepted, 40 EAU go to the vault to pay off the debt and 20 TKN go to the buyer's account
     */
    @Test
    fun buyTokensWhenVaultBreached() {
        val price = toTokenAmount(2)
        ownerCreatesVault(tokenPrice = price)
        ownerBreachesVault()
        val costInEau = getEauToBuyUserTokenAmount(toBuy, tokenPrice = price)
        helper.addEAU(buyer.address, costInEau)

        eauTokenByBuyer.approve(vaultByOwner.contractAddress, costInEau).send()
        vaultByBuyer.buy(toBuy, tokenPrice, buyer.address).send()

        assertEquals(BigInteger.ZERO, eauToken.balanceOf(vaultByOwner.contractAddress).send())
        assertEquals(BigInteger.ZERO, eauToken.balanceOf(buyer.address).send())
        assertEquals(initialAmount.minus(toBuy), userToken.balanceOf(vaultByOwner.contractAddress).send())
        assertEquals(toBuy, userToken.balanceOf(buyer.address).send())
    }

    /**
     * @given A breached vault with closed out process initiated 30*60*100 = 180000 seconds ago. Dutch auction is
     * over and TKN price is zero now.
     * @when buyer buys a token
     * @then error returned - auction is over
     */
    @Test
    fun buyTokensWhenInitialLiquidityAuctionIsOver() {
        ownerCreatesVault()
        ownerBreachesVault()
        val costInEau = getEauToBuyUserTokenAmount(toBuy)
        helper.addEAU(buyer.address, costInEau)
        vaultByOwner.startInitialLiquidityAuction().send()
        helper.passTime(BigInteger.valueOf(30 * 60 * 100))

        assertEquals(BigInteger.ZERO, vaultByOwner.price.send())

        eauTokenByBuyer.approve(vaultByOwner.contractAddress, costInEau).send()
        assertThrows<TransactionException> {
            vaultByBuyer.buy(toBuy, tokenPrice, buyer.address).send()
        }
    }

    /**
     * @given A breached vault with close-out process started. The vault has 100 TKN at price TKN/EAU = 2 and debt is
     * 50 EAU. Credit limit is 50 EAU.
     * @when a buyer buys 1 TKN for 2 EAU
     * @then the debt is paid off partially and breach is covered. Vault debt is 48 EAU, credit limit is 49,5 EAU (for 99
     * TKN) and can borrow 1 EAU
     */
    @Test
    fun buyTokensToCoverBreach() {
        val initialSuppty = toTokenAmount(100)
        val price = toTokenAmount(2)
        ownerCreatesVault(initialAmount = initialSuppty, tokenPrice = price)
        val debtBefore = vaultByOwner.canBorrow().send()
        ownerBreachesVault()
        vaultByOwner.startInitialLiquidityAuction().send()
        val toBuy = toTokenAmount(1)
        val costInEau = getEauToBuyUserTokenAmount(toBuy, tokenPrice = price)
        helper.addEAU(buyer.address, costInEau)
        assertEquals(true, vaultByOwner.isLimitBreached.send())

        eauTokenByBuyer.approve(vaultByOwner.contractAddress, costInEau).send()
        vaultByBuyer.buy(toBuy, tokenPrice, buyer.address).send()

        assertEquals(false, vaultByOwner.isLimitBreached.send())
        // 10% is penalty, the rest to pay off
        assertEquals(
            debtBefore.minus(costInEau.minus(costInEau.divide(BigInteger.TEN))),
            vaultByOwner.totalDebt.send()
        )
        assertEquals(BigInteger("49500000000000000000"), vaultByOwner.creditLimit.send())
        assertEquals(BigInteger("1300000000000000000"), vaultByOwner.canBorrow().send())
    }

    /**
     * @given A breached vault with close-out process started 30*60*50 = 90000 seconds ago. Price TKN/EAU discounted
     * for 50% and = 1 now. The vault has 100 TKN and debt is 50 EAU and maximum credit limit is 25 EAU (25% of 100 TKN
     * at price 1 TKN/EAU).
     * @when a buyer buys 20 TKN for 20 EAU
     * @then the debt is paid off partially and breach is not covered
     */
    @Test
    fun buyTokensToCoverBreachAfterTime() {
        val price = toTokenAmount(2)
        ownerCreatesVault(tokenPrice = price)
        val toBorrow = vaultByOwner.canBorrow().send()
        ownerBreachesVault()
        vaultByOwner.startInitialLiquidityAuction().send()
        helper.passTime(BigInteger.valueOf(30 * 60 * 50))
        val toBuy = toTokenAmount(20)
        val currentPrice = vaultByOwner.price.send()
        assertEquals(toTokenAmount(1), currentPrice)
        val costInEau = getEauToBuyUserTokenAmount(toBuy, tokenPrice = currentPrice)
        helper.addEAU(buyer.address, costInEau)
        assertEquals(true, vaultByOwner.isLimitBreached.send())
        // add CLGN to swap for penalty
        val penaltyInClgn = toBuy.div(BigInteger.TEN).div(helper.clgnEauPrice)
        helper.addCLGN(helper.marketAdaptor.contractAddress, penaltyInClgn)

        eauTokenByBuyer.approve(vaultByOwner.contractAddress, costInEau).send()
        vaultByBuyer.buy(toBuy, currentPrice, buyer.address).send()

        assertEquals(true, vaultByOwner.isLimitBreached.send())
        // old debt - (payed in EAU - 10% penalty)
        val expectedDebt = toBorrow.minus(costInEau.minus(costInEau.divide(BigInteger.TEN)))
        assertEquals(expectedDebt, vaultByOwner.totalDebt.send())
        assertEquals(BigInteger.ZERO, vaultByOwner.canBorrow().send())
    }

    /**
     * @given A breached vault with close-out process started. The vault has 8000 TKN and debt is 2000 EAU and price is
     * 1 TKN/EAU and CLGN/EAU price = 2.
     * @when a buyer buys 2000 TKN for 2000 EAU
     * @then the penalty is 200 EAU (10%) used to buy 100 CLGN
     * - 33 CLGN goes to the initator
     * - 33 CLGN goes to buyer
     * - 34 CLGN burnt
     */
    @Test
    fun distributeBounty() {
        val toBuy = toTokenAmount(2000)
        val price = toTokenAmount(1)
        val costInEau = getEauToBuyUserTokenAmount(toBuy, tokenPrice = price)
        helper.addEAU(buyer.address, costInEau)
        ownerCreatesVault(initialAmount = toTokenAmount(8000), tokenPrice = price)
        ownerBreachesVault()
        vaultByInitiator.startInitialLiquidityAuction().send()
        // add CLGN to swap for penalty
        val penaltyInClgn = toBuy.div(BigInteger.TEN).div(helper.clgnEauPrice)
        helper.addCLGN(helper.marketAdaptor.contractAddress, penaltyInClgn)
        val clgnSupply = clgnToken.totalSupply().send()

        eauTokenByBuyer.approve(vaultByOwner.contractAddress, costInEau).send()
        vaultByBuyer.buy(toBuy, price, buyer.address).send()

        assertEquals(toTokenAmount(33), clgnToken.balanceOf(initiator.address).send())
        assertEquals(toTokenAmount(33), clgnToken.balanceOf(buyer.address).send())
        assertEquals(clgnSupply.minus(toTokenAmount(34)), clgnToken.totalSupply().send())
    }
}
