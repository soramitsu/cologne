package acceptance

import contract.EAUToken
import contract.CLGNToken
import contract.UserToken
import contract.Vault
import helpers.ContractTestHelper
import helpers.VaultState
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.testcontainers.containers.GenericContainer
import org.testcontainers.images.builder.ImageFromDockerfile
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.web3j.crypto.Credentials
import org.web3j.protocol.exceptions.TransactionException
import java.math.BigInteger
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertEquals

@Testcontainers
class InitialLiquidityAuctionAcceptanceTest {

    @Container
    private val ganache: GenericContainer<Nothing> =
        GenericContainer<Nothing>(
            ImageFromDockerfile()
                .withDockerfile(Path.of(javaClass.getResource("/docker/ganache/Dockerfile").toURI()))
        )
            .withExposedPorts(8545)

    val toBuy = BigInteger.valueOf(20)
    val initialAmount = BigInteger.valueOf(100)
    val tokenPrice = BigInteger.valueOf(2)
    lateinit var helper: ContractTestHelper
    lateinit var owner: Credentials
    lateinit var intitator: Credentials
    lateinit var buyer: Credentials
    lateinit var userToken: UserToken
    lateinit var vault: Vault
    lateinit var intiatorVault: Vault
    lateinit var eauToken: EAUToken
    lateinit var buyerEAUToken: EAUToken
    lateinit var buyerVault: Vault
    lateinit var clgnToken: CLGNToken

    @BeforeEach
    fun setUp() {
        helper = ContractTestHelper(ganache.host, ganache.firstMappedPort)
        owner = helper.credentialsAlice
        intitator = helper.credentialsBob
        buyer = helper.credentialsCharlie
        // load UserToken with owner credentials
        userToken =
            UserToken.load(helper.userToken.contractAddress, helper.web3, helper.credentialsSeed, helper.gasProvider)
        // load EAU token with owner credentials
        eauToken =
            EAUToken.load(helper.eauToken.contractAddress, helper.web3, helper.credentialsSeed, helper.gasProvider)
        buyerEAUToken = EAUToken.load(eauToken.contractAddress, helper.web3, buyer, helper.gasProvider)
        clgnToken = helper.clgnToken
    }

    /**
     * Deploy vault with Owner credentials
     */
    fun ownerCreatesVault(amount: BigInteger = initialAmount, price: BigInteger = tokenPrice) {
        val stake = BigInteger.valueOf(20)
        val vaultAddress = helper.createVault(owner, stake, amount, price)
        vault = Vault.load(vaultAddress, helper.web3, owner, helper.gasProvider)
        intiatorVault = Vault.load(vault.contractAddress, helper.web3, intitator, helper.gasProvider)
        buyerVault = Vault.load(vault.contractAddress, helper.web3, buyer, helper.gasProvider)
    }

    /**
     * Breach credit limit
     */
    fun breachVault() {
        val toBorrow = vault.creditLimit.send()
        vault.borrow(toBorrow).send()
    }

    /**
     * @given a vault with limit not breached
     * @when close-out initiator calls startInitialLiquidityAuction()
     * @then error returned - the vault is not in breach
     */
    @Test
    fun closeOutWithoutBreach() {
        ownerCreatesVault()

        assertThrows<TransactionException> {
            intiatorVault.startInitialLiquidityAuction().send()
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
        vault.close().send()

        assertThrows<TransactionException> {
            intiatorVault.startInitialLiquidityAuction().send()
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
        breachVault()
        intiatorVault.startInitialLiquidityAuction().send()

        assertThrows<TransactionException> {
            intiatorVault.startInitialLiquidityAuction().send()
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
        val price = BigInteger.valueOf(100000)
        ownerCreatesVault(price = price)
        breachVault()
        assertEquals(VaultState.Defaulted.toBigInteger(), vault.state.send())
        intiatorVault.startInitialLiquidityAuction().send()
        assertEquals(VaultState.InitialLiquidityAuctionInProcess.toBigInteger(), vault.state.send())

        assertEquals(price, vault.price.send())

        helper.passTime(timeInterval)
        assertEquals(BigInteger.valueOf(99000), vault.price.send())
        assertEquals(VaultState.InitialLiquidityAuctionInProcess.toBigInteger(), vault.state.send())

        helper.passTime(timeInterval)
        assertEquals(BigInteger.valueOf(98000), vault.price.send())
        assertEquals(VaultState.InitialLiquidityAuctionInProcess.toBigInteger(), vault.state.send())

        helper.passTime(timeInterval.multiply(BigInteger.valueOf(97)))
        assertEquals(BigInteger.valueOf(1000), vault.price.send())
        assertEquals(VaultState.InitialLiquidityAuctionInProcess.toBigInteger(), vault.state.send())

        helper.passTime(timeInterval)
        assertEquals(BigInteger.ZERO, vault.price.send())
        assertEquals(VaultState.WaitingForSlashing.toBigInteger(), vault.state.send())
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
        ownerCreatesVault()
        breachVault()
        val costInEau = toBuy.multiply(tokenPrice)
        helper.addEAU(buyer.address, costInEau)

        buyerEAUToken.approve(vault.contractAddress, costInEau).send()
        buyerVault.buy(toBuy, tokenPrice, buyer.address).send()

        assertEquals(BigInteger.ZERO, eauToken.balanceOf(vault.contractAddress).send())
        assertEquals(BigInteger.ZERO, eauToken.balanceOf(buyer.address).send())
        assertEquals(initialAmount.minus(toBuy), userToken.balanceOf(vault.contractAddress).send())
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
        breachVault()
        val costInEau = toBuy.multiply(tokenPrice)
        helper.addEAU(buyer.address, costInEau)
        vault.startInitialLiquidityAuction().send()
        helper.passTime(BigInteger.valueOf(30 * 60 * 100))

        assertEquals(BigInteger.ZERO, vault.price.send())

        buyerEAUToken.approve(vault.contractAddress, costInEau).send()
        assertThrows<TransactionException> {
            buyerVault.buy(toBuy, tokenPrice, buyer.address).send()
        }
    }

    /**
     * @given A breached vault with close-out process started. The vault has 100 TKN at price TKN/EAU = 2 and debt is
     * 50 EAU.
     * @when a buyer buys 1 TKN for 2 EAU
     * @then the debt is paid off partially and breach is covered
     */
    @Test
    fun buyTokensToCoverBreach() {
        ownerCreatesVault()
        val toBorrow = vault.creditLimit.send()
        breachVault()
        vault.startInitialLiquidityAuction().send()
        val toBuy = BigInteger.ONE
        val costInEau = toBuy.multiply(tokenPrice)
        helper.addEAU(buyer.address, costInEau)
        assertEquals(true, vault.isLimitBreached.send())

        buyerEAUToken.approve(vault.contractAddress, costInEau).send()
        buyerVault.buy(toBuy, tokenPrice, buyer.address).send()

        assertEquals(false, vault.isLimitBreached.send())
        assertEquals(toBorrow.minus(costInEau), vault.totalDebt.send())
        assertEquals(costInEau, vault.creditLimit.send())
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
        ownerCreatesVault()
        val toBorrow = vault.creditLimit.send()
        breachVault()
        vault.startInitialLiquidityAuction().send()
        helper.passTime(BigInteger.valueOf(30 * 60 * 50))
        val toBuy = BigInteger.valueOf(20)
        val price = vault.price.send()
        assertEquals(BigInteger.ONE, price)
        val costInEau = toBuy.multiply(price)
        helper.addEAU(buyer.address, costInEau)
        assertEquals(true, vault.isLimitBreached.send())
        // add CLGN to swap for penalty
        val penaltyInClgn = toBuy.div(BigInteger.TEN).div(helper.clgnEauPrice)
        helper.addCLGN(helper.marketAdaptor.contractAddress, penaltyInClgn)

        buyerEAUToken.approve(vault.contractAddress, costInEau).send()
        buyerVault.buy(toBuy, price, buyer.address).send()

        assertEquals(true, vault.isLimitBreached.send())
        // old debt - (payed in EAU - 10% penalty)
        val expectedDebt = toBorrow.minus(costInEau.minus(costInEau.divide(BigInteger.TEN)))
        assertEquals(expectedDebt, vault.totalDebt.send())
        assertEquals(BigInteger.ZERO, vault.creditLimit.send())
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
        val toBuy = BigInteger.valueOf(2000)
        val price = BigInteger.ONE
        val costInEau = toBuy.multiply(price)
        helper.addEAU(buyer.address, costInEau)
        ownerCreatesVault(amount = BigInteger.valueOf(8000), price = price)
        breachVault()
        intiatorVault.startInitialLiquidityAuction().send()
        // add CLGN to swap for penalty
        val penaltyInClgn = toBuy.div(BigInteger.TEN).div(helper.clgnEauPrice)
        helper.addCLGN(helper.marketAdaptor.contractAddress, penaltyInClgn)
        val clgnSupply = clgnToken.totalSupply().send()

        buyerEAUToken.approve(vault.contractAddress, costInEau).send()
        buyerVault.buy(toBuy, price, buyer.address).send()

        assertEquals(BigInteger.valueOf(33), clgnToken.balanceOf(intitator.address).send())
        assertEquals(BigInteger.valueOf(33), clgnToken.balanceOf(buyer.address).send())
        assertEquals(clgnSupply.minus(BigInteger.valueOf(34)), clgnToken.totalSupply().send())
    }
}
