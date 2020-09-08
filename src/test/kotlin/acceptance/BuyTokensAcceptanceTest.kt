package acceptance

import contract.*
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
class BuyTokensAcceptanceTest {

    @Container
    private val ganache: GenericContainer<Nothing> =
        GenericContainer<Nothing>(
            ImageFromDockerfile()
                .withDockerfile(Path.of(javaClass.getResource("/docker/ganache/Dockerfile").toURI()))
        )
            .withExposedPorts(8545)

    lateinit var helper: ContractTestHelper
    lateinit var buyer: Credentials
    lateinit var userToken: UserToken
    lateinit var eauToken: EAUToken
    lateinit var buyerEAUToken: EAUToken
    val initialAmount = BigInteger.valueOf(100)
    val tokenPrice = BigInteger.valueOf(2)
    lateinit var vault: Vault
    lateinit var buyerVault: Vault
    val toBuy = BigInteger.valueOf(20)

    @BeforeEach
    fun setUp() {
        helper = ContractTestHelper(ganache.host, ganache.firstMappedPort)
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
    fun ownerCreatesVault(amount: BigInteger = initialAmount) {
        val stake = BigInteger.valueOf(20)
        val vaultAddress = helper.createVault(helper.credentialsAlice, stake, amount, tokenPrice)
        vault = Vault.load(vaultAddress, helper.web3, helper.credentialsAlice, helper.gasProvider)
        buyerVault = Vault.load(vault.contractAddress, helper.web3, buyer, helper.gasProvider)
    }

    /**
     * @given a vault has at least 20 TKN (user tokens) and a buyer has 40 EAU and price is 2 TKN/EAU
     * @when the buyer buys 20 TKN at price at least 2 TKN/EAU
     * @then 40 EAU go to the vault and 20 TKN go to the buyer's account
     */
    @Test
    fun buyTokens() {
        ownerCreatesVault()
        val costInEau = toBuy.multiply(tokenPrice)
        eauToken.mint(buyer.address, costInEau).send()

        buyerEAUToken.approve(vault.contractAddress, costInEau).send()
        buyerVault.buy(toBuy, tokenPrice, buyer.address).send()

        assertEquals(costInEau, eauToken.balanceOf(vault.contractAddress).send())
        assertEquals(BigInteger.ZERO, eauToken.balanceOf(buyer.address).send())
        assertEquals(initialAmount.minus(toBuy), userToken.balanceOf(vault.contractAddress).send())
        assertEquals(toBuy, userToken.balanceOf(buyer.address).send())
    }

    /**
     * @given a vault has insufficient user token (1 TKN) and a buyer has 40 EAU and price is 2 TKN/EAU
     * @when the buyer buys 20 TKN with price at least 2 TKN/EAU
     * @then deal rejected, balances are not changed
     */
    @Test
    fun buyTokensNotEnoughUserTokens() {
        val insufficientAmount = BigInteger.ONE
        ownerCreatesVault(amount = insufficientAmount)
        val costInEau = toBuy.multiply(tokenPrice)
        eauToken.mint(buyer.address, costInEau).send()

        buyerEAUToken.approve(vault.contractAddress, costInEau).send()
        assertThrows<TransactionException> {
            buyerVault.buy(toBuy, tokenPrice, buyer.address).send()
        }

        assertEquals(BigInteger.ZERO, eauToken.balanceOf(vault.contractAddress).send())
        assertEquals(costInEau, eauToken.balanceOf(buyer.address).send())
        assertEquals(insufficientAmount, userToken.balanceOf(vault.contractAddress).send())
        assertEquals(BigInteger.ZERO, userToken.balanceOf(buyer.address).send())
    }

    /**
     * @given a vault has enough user token and a buyer with 40 EAU and price is 2 TKN/EAU
     * @when the buyer buys 20 TKN with price at least 1 TKN/EAU
     * @then deal rejected, balances are not changed
     */
    @Test
    fun buyTokensPriceTooLow() {
        ownerCreatesVault()
        val priceTooHigh = BigInteger.ONE
        val costInEau = toBuy.multiply(priceTooHigh)
        eauToken.mint(buyer.address, costInEau).send()

        buyerEAUToken.approve(vault.contractAddress, costInEau).send()
        assertThrows<TransactionException> {
            buyerVault.buy(toBuy, priceTooHigh, buyer.address).send()
        }

        assertEquals(BigInteger.ZERO, eauToken.balanceOf(vault.contractAddress).send())
        assertEquals(costInEau, eauToken.balanceOf(buyer.address).send())
        assertEquals(initialAmount, userToken.balanceOf(vault.contractAddress).send())
        assertEquals(BigInteger.ZERO, userToken.balanceOf(buyer.address).send())
    }
}
