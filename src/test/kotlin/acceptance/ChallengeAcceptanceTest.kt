package acceptance

import contract.Vault
import helpers.VaultState
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.testcontainers.junit.jupiter.Testcontainers
import org.web3j.crypto.Credentials
import org.web3j.protocol.exceptions.TransactionException
import java.math.BigInteger
import org.junit.jupiter.api.Assertions.assertEquals

@Testcontainers
class ChallengeAcceptanceTest : AcceptanceTest() {

    fun challenge(caller: Credentials, price: BigInteger, eauToLock: BigInteger) {
        helper.addAndApproveEAU(caller, vaultByOwner.contractAddress, eauToLock)
        val vaultByCaller = Vault.load(vaultByOwner.contractAddress, helper.web3, caller, helper.gasProvider)
        vaultByCaller.challenge(price, eauToLock).send()
    }

    /**
     * @given a vault with no challengers
     * @when get challenge winner called
     * @then zero values returned
     */
    @Test
    fun getDefaultChallengeWinner() {
        ownerCreatesVault()

        val (address, price) = vaultByOwner.challengeWinner.send()

        assertEquals("0x0000000000000000000000000000000000000000", address)
        assertEquals(BigInteger.ZERO, price)
    }

    /**
     * @given Initial Liquidity Auction is over
     * @when challenge called
     * @then failed - cannot be challenged after initial liquidity auction
     */
    @Test
    fun initialLiquidityAuctionIsOver() {
        ownerCreatesVault()
        val challenger = helper.credentialsCharlie
        ownerBreachesVault()
        startInitialAuction()
        failInitialAuction()
        assertEquals(VaultState.WaitingForSlashing.toBigInteger(), vaultByOwner.state.send())

        assertThrows<TransactionException> {
            challenge(challenger, BigInteger.ONE, BigInteger.TEN)
        }
    }

    /**
     * @given a vault has 100 User Tokens at price 10 EAU (total assessed amount is 1000 EAU
     * @when challenger proposes challenge with lock amount 10 EAU
     * @then error returned 10 < 1000 - lock EAU not enough
     */
    @Test
    fun eauLockedNotEnough() {
        val initialAmount = toTokenAmount(100)
        val tokenPrice = toTokenAmount(10)
        ownerCreatesVault(initialAmount, tokenPrice)
        val challenger = helper.credentialsCharlie
        val vaultByChallenger = Vault.load(vaultByOwner.contractAddress, helper.web3, challenger, helper.gasProvider)

        val eauToLock = toTokenAmount(10)

        assertThrows<TransactionException> {
            vaultByChallenger.challenge(BigInteger.ONE, eauToLock).send()
        }
    }

    /**
     * @given a vault with price 10 EAU
     * @when challenger proposes challenge with price 10 EAU
     * @then error returned - price too high
     */
    @Test
    fun priceTooHogh() {
        val initialAmount = toTokenAmount(100)
        val tokenPrice = toTokenAmount(10)
        ownerCreatesVault(initialAmount, tokenPrice)

        val challenger = helper.credentialsCharlie
        val vaultByChallenger = Vault.load(vaultByOwner.contractAddress, helper.web3, challenger, helper.gasProvider)

        val eauToLock = toTokenAmount(10_000)

        assertThrows<TransactionException> {
            vaultByChallenger.challenge(tokenPrice, eauToLock).send()
        }
    }

    /**
     * @given a vault with price 10 EAU and challenge bid is 5 EAU
     * @when challenger2 proposes challenge with price 7 EAU
     * @then challenge winner now is challenger2, his amount is locked
     * challenger1 amount is unlocked and can be redeemed
     */
    @Test
    fun overbidPrice() {
        val initialAmount = toTokenAmount(100)
        val tokenPrice = toTokenAmount(10)
        ownerCreatesVault(initialAmount, tokenPrice)

        val challenger = helper.credentialsCharlie
        val challenger2 = helper.credentialsDave
        val vaultByChallenger = Vault.load(vaultByOwner.contractAddress, helper.web3, challenger, helper.gasProvider)
        val vaultByChallenger2 = Vault.load(vaultByOwner.contractAddress, helper.web3, challenger2, helper.gasProvider)

        val bidPrice = toTokenAmount(5)
        var eauLocked = getEauToBuyUserTokenAmount(vaultByOwner.tokenAmount.send(), tokenPrice = bidPrice)
        challenge(challenger, bidPrice, eauLocked)
        val (address, price) = vaultByChallenger.challengeWinner.send()
        assertEquals(challenger.address, address)
        assertEquals(bidPrice, price)
        assertEquals(eauLocked, vaultByChallenger.getChallengeLocked(challenger.address).send())

        val overbidPrice = toTokenAmount(7)
        eauLocked = getEauToBuyUserTokenAmount(vaultByOwner.tokenAmount.send(), tokenPrice = overbidPrice)
        challenge(challenger2, overbidPrice, eauLocked)
        val (address2, price2) = vaultByChallenger.challengeWinner.send()
        assertEquals(challenger2.address, address2)
        assertEquals(overbidPrice, price2)
        assertEquals(BigInteger.ZERO, vaultByChallenger.getChallengeLocked(challenger.address).send())
        assertEquals(eauLocked, vaultByChallenger2.getChallengeLocked(challenger2.address).send())
    }

    /**
     * @given a vault with price 10 EAU and 100 User Tokens and challenger challenge for 2000 EAU and price is 5 EAU
     * 500 EAU actually locked (5 EAU * 100 TKN), 1500 EAU unlocked and can be redeemed
     * @when challenger redeems
     * @then challenger gets 1500 EAU and 500 EAU are locked
     */
    @Test
    fun getRedeemUnlocked() {
        val initialAmount = toTokenAmount(100)
        val tokenPrice = toTokenAmount(10)
        ownerCreatesVault(initialAmount, tokenPrice)
        val challenger = helper.credentialsCharlie
        val vaultByChallenger = Vault.load(vaultByOwner.contractAddress, helper.web3, challenger, helper.gasProvider)

        val bidPrice = toTokenAmount(5)
        val eauToLock = toTokenAmount(2_000)
        challenge(challenger, bidPrice, eauToLock)
        var redeemable = vaultByChallenger.getRedeemableChallenge(challenger.address).send()
        assertEquals(toTokenAmount(1500), redeemable.component1())
        assertEquals(BigInteger.ZERO, redeemable.component2())
        val balanceBefore = helper.eauToken.balanceOf(challenger.address).send()

        vaultByChallenger.redeemChallenge().send()

        assertEquals(balanceBefore + toTokenAmount(1500), helper.eauToken.balanceOf(challenger.address).send())
        redeemable = vaultByChallenger.getRedeemableChallenge(challenger.address).send()
        assertEquals(BigInteger.ZERO, redeemable.component1())
        assertEquals(BigInteger.ZERO, redeemable.component2())
    }

    /**
     * @given a vault with price 10 EAU and 100 User Tokens and challenger challenge for 2000 EAU and price is 5 EAU
     * 500 EAU actually locked (5 EAU * 100 TKN), 1500 EAU unlocked and can be redeemed, and 10 User Token bought,
     * so 450 EAU locked and 1550 EAU are unlocked and Initial Liquidity auction has passed
     * @when challenger redeems
     * @then the challenger get User Tokens (90 TKN and 1550 EAU) and 450 EAU goes to pay off debt and
     * the challenger gets bounty (450 * 10% / 3 = 15 EAU = 15 EAU / 2 EAU/CLGN = 7 CLGN)
     */
    @Test
    fun challengeBuyTokens() {
        val initialAmount = toTokenAmount(100)
        val tokenPrice = toTokenAmount(10)
        ownerCreatesVault(initialAmount, tokenPrice)

        val challenger = helper.credentialsDave
        val vaultByChallenger = Vault.load(vaultByOwner.contractAddress, helper.web3, challenger, helper.gasProvider)

        val bidPrice = toTokenAmount(5)
        val eauToLock = toTokenAmount(2_000)
        challenge(challenger, bidPrice, eauToLock)
        var redeemable = vaultByChallenger.getRedeemableChallenge(challenger.address).send()
        assertEquals(toTokenAmount(1500), redeemable.component1())
        assertEquals(BigInteger.ZERO, redeemable.component2())
        val eauBalanceBefore = helper.eauToken.balanceOf(challenger.address).send()
        val tknBalanceBefore = helper.userToken.balanceOf(challenger.address).send()

        helper.addAndApproveEAU(buyer, vaultByBuyer.contractAddress, toTokenAmount(100))
        vaultByBuyer.buy(toTokenAmount(10), tokenPrice, buyer.address).send()
        redeemable = vaultByChallenger.getRedeemableChallenge(challenger.address).send()
        assertEquals(toTokenAmount(1550), redeemable.component1())
        assertEquals(BigInteger.ZERO, redeemable.component2())

        ownerBreachesVault()
        startInitialAuction()
        failInitialAuction()
        assertEquals(VaultState.SoldOut.toBigInteger(), vaultByOwner.state.send())

        redeemable = vaultByChallenger.getRedeemableChallenge(challenger.address).send()
        assertEquals(toTokenAmount(1550), redeemable.component1())
        assertEquals(toTokenAmount(90), redeemable.component2())

        helper.addCLGN(helper.marketAdaptor.contractAddress, toTokenAmount(1000))

        vaultByChallenger.redeemChallenge().send()

        assertEquals( eauBalanceBefore + toTokenAmount(1550), helper.eauToken.balanceOf(challenger.address).send())
        assertEquals(tknBalanceBefore + toTokenAmount(90), helper.userToken.balanceOf(challenger.address).send())
        redeemable = vaultByChallenger.getRedeemableChallenge(challenger.address).send()
        assertEquals(BigInteger.ZERO, redeemable.component1())
        assertEquals(BigInteger.ZERO, redeemable.component2())
        // check bounty
        assertEquals(BigInteger("7425000000000000000"), helper.clgnToken.balanceOf(challenger.address).send())
    }

    /**
     * @given the wallet and challenges the owner has closed the wallet
     * @when challengers redeem
     * @then challengers get redeem amounts
     */
    @Test
    fun challengeRedeemAfterClose() {
        val initialAmount = toTokenAmount(100)
        val tokenPrice = toTokenAmount(10)
        ownerCreatesVault(initialAmount, tokenPrice)

        val challenger = helper.credentialsCharlie
        val challenger2 = helper.credentialsDave
        val vaultByChallenger = Vault.load(vaultByOwner.contractAddress, helper.web3, challenger, helper.gasProvider)
        val vaultByChallenger2 = Vault.load(vaultByOwner.contractAddress, helper.web3, challenger2, helper.gasProvider)

        val bidPrice = toTokenAmount(5)
        val eauToLock = toTokenAmount(2_000)
        challenge(challenger, bidPrice, eauToLock)

        // challenger2 is winner now
        val overbidPrice = toTokenAmount(7)
        val eauToLock2 = toTokenAmount(3_000)
        challenge(challenger2, overbidPrice, eauToLock2)

        // vault balances before
        val (vaultTknBalance, vaultEauBalance, _) = helper.getBalances(vaultByOwner.contractAddress)
        assertEquals(initialAmount, vaultTknBalance)
        assertEquals(eauToLock + eauToLock2, vaultEauBalance)

        // owner balances before
        val (ownerTknBalance, ownerEauBalance, _) = helper.getBalances(owner.address)
        assertEquals(BigInteger.ZERO, ownerTknBalance)
        assertEquals(BigInteger.ZERO, ownerEauBalance)

        vaultByOwner.close().send()

        // vault balances after, challenger's eau are on the vault
        val (vaultTknBalanceAfter, vaultEauBalanceAfter, _) = helper.getBalances(vaultByOwner.contractAddress)
        assertEquals(BigInteger.ZERO, vaultTknBalanceAfter)
        assertEquals(eauToLock + eauToLock2, vaultEauBalanceAfter)

        val (ownerTknBalanceAfter, ownerEauBalanceAfter, _) = helper.getBalances(owner.address)
        assertEquals(initialAmount, ownerTknBalanceAfter)
        assertEquals(BigInteger.ZERO, ownerEauBalanceAfter)

        // challenger redeem
        vaultByChallenger.redeemChallenge().send()
        val (challengerTkn, challengerEau, _) = helper.getBalances(challenger.address)
        assertEquals(BigInteger.ZERO, challengerTkn)
        assertEquals(eauToLock, challengerEau)

        // challenger2 redeem
        vaultByChallenger2.redeemChallenge().send()
        val (challenger2Tkn, challenger2Eau, _) = helper.getBalances(challenger2.address)
        assertEquals(BigInteger.ZERO, challenger2Tkn)
        assertEquals(eauToLock2, challenger2Eau)
    }
}
