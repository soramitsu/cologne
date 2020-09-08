package acceptance

import contract.EAUToken
import contract.UserToken
import contract.Vault
import helpers.ContractTestHelper
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
class CloseOutAcceptanceTest {

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
    lateinit var intitator: Credentials
    lateinit var buyer: Credentials
    lateinit var userToken: UserToken
    lateinit var vault: Vault
    lateinit var intiatorVault: Vault
    lateinit var eauToken: EAUToken
    lateinit var buyerEAUToken: EAUToken
    lateinit var buyerVault: Vault

    @BeforeEach
    fun setUp() {
        helper = ContractTestHelper(ganache.host, ganache.firstMappedPort)
        intitator = helper.credentialsBob
        buyer = helper.credentialsBob
        // load UserToken with owner credentials
        userToken =
            UserToken.load(helper.userToken.contractAddress, helper.web3, helper.credentialsAlice, helper.gasProvider)
        // load EAU token with owner credentials
        eauToken =
            EAUToken.load(helper.eauToken.contractAddress, helper.web3, helper.credentialsAlice, helper.gasProvider)
        buyerEAUToken = EAUToken.load(eauToken.contractAddress, helper.web3, buyer, helper.gasProvider)
    }


    /**
     * Deploy vault with Owner credentials
     */
    fun ownerCreatesVault(price: BigInteger = tokenPrice) {
        val stake = BigInteger.valueOf(20)
        val vaultAddress = helper.createVault(helper.credentialsAlice, stake, initialAmount, price)
        vault = Vault.load(vaultAddress, helper.web3, helper.credentialsAlice, helper.gasProvider)
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
        intiatorVault.startInitialLiquidityAuction().send()

        assertEquals(price, vault.price.send())

        helper.passTime(timeInterval)
        assertEquals(BigInteger.valueOf(99000), vault.price.send())

        helper.passTime(timeInterval)
        assertEquals(BigInteger.valueOf(98000), vault.price.send())

        helper.passTime(timeInterval.multiply(BigInteger.valueOf(97)))
        assertEquals(BigInteger.valueOf(1000), vault.price.send())

        helper.passTime(timeInterval)
        assertEquals(BigInteger.ZERO, vault.price.send())
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
        val toBorrow = vault.creditLimit.send()
        vault.borrow(toBorrow).send()
        val costInEau = toBuy.multiply(tokenPrice)
        eauToken.mint(buyer.address, costInEau).send()

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
        val toBorrow = vault.creditLimit.send()
        vault.borrow(toBorrow).send()
        val costInEau = toBuy.multiply(tokenPrice)
        eauToken.mint(buyer.address, costInEau).send()
        vault.startInitialLiquidityAuction().send()
        helper.passTime(BigInteger.valueOf(30 * 60 * 100))

        assertEquals(BigInteger.ZERO, vault.price.send())

        buyerEAUToken.approve(vault.contractAddress, costInEau).send()
        assertThrows<TransactionException> {
            buyerVault.buy(toBuy, tokenPrice, buyer.address).send()
        }
    }

    // TODO buy - check penalty distribution

    // TODO buy at initial auction to raise enough funds to cover the debt
}
