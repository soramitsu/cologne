import contract.MDLYToken
import contract.MedleyDAO
import contract.UserToken
import contract.Vault
import helpers.ContractTestHelper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.testcontainers.containers.GenericContainer
import org.testcontainers.images.builder.ImageFromDockerfile
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.web3j.protocol.core.DefaultBlockParameter
import org.web3j.protocol.exceptions.TransactionException
import java.math.BigInteger
import java.nio.file.Path

@Testcontainers
class VaultTest {

    @Container
    private val ganache: GenericContainer<Nothing> =
        GenericContainer<Nothing>(
            ImageFromDockerfile()
                .withDockerfile(Path.of(javaClass.getResource("docker/ganache/Dockerfile").toURI()))
        )
            .withExposedPorts(8545)

    lateinit var helper: ContractTestHelper
    lateinit var medleyDAO: MedleyDAO
    lateinit var owner: String
    lateinit var userToken: UserToken
    lateinit var mdlyToken: MDLYToken
    val stake = BigInteger.valueOf(20)
    val initialAmount = BigInteger.valueOf(100)
    val tokenPrice = BigInteger.valueOf(2)
    lateinit var vault: Vault

    @BeforeEach
    fun setUp() {
        helper = ContractTestHelper(ganache.host, ganache.firstMappedPort)
        owner = helper.credentialsAlice.address
        // load medley with Alice credentials
        medleyDAO =
            MedleyDAO.load(helper.medleyDAO.contractAddress, helper.web3, helper.credentialsAlice, helper.gasProvider)
        // load UserToken with owner credentials
        userToken =
            UserToken.load(helper.userToken.contractAddress, helper.web3, helper.credentialsAlice, helper.gasProvider)
        // load MDLY token with owner credentials
        mdlyToken =
            MDLYToken.load(helper.mdlyToken.contractAddress, helper.web3, helper.credentialsAlice, helper.gasProvider)

        ownerCreatesVault()
    }

    /**
     * Deploy vault with Owner credentials
     */
    fun ownerCreatesVault() {
        val vaultAddress = helper.createVault(helper.credentialsAlice, stake, initialAmount, tokenPrice)
        vault = Vault.load(vaultAddress, helper.web3, helper.credentialsAlice, helper.gasProvider)
    }

    /**
     * @given MedleyDAO deployed and no vaults created and user has 10 UserTokens
     * @when create vault called with 10 UserTokens by owner
     * @then new vault created and vault owner is caller and 10 UserTokens transferred to the vault and credit limit is
     * 25% of assessed value (100 TKN * 2 EAU / 4 = 50 EAU)
     */
    @Test
    fun createVault() {
        assertEquals(initialAmount, userToken.balanceOf(vault.contractAddress).send())
        assertEquals(BigInteger.ZERO, userToken.balanceOf(owner).send())
        assertEquals(tokenPrice, vault.price.send())
        assertEquals(BigInteger.valueOf(50), vault.creditLimit.send())
    }

    /**
     * @given MedleyDAO deployed owner can borrow 50 EAU
     * @when the owner borrows 50 EAU
     * @then EAU tokens are minted to the owner account, owner debt is 50 EAU
     */
    @Test
    fun borrowSunnyDay() {
        val initialEauSupply = helper.eauToken.totalSupply().send()
        val initialOwnerEauBalance = helper.eauToken.balanceOf(owner).send()
        val toBorrow = vault.creditLimit.send()

        val tx = vault.borrow(toBorrow).send()

        assertEquals(initialEauSupply.plus(toBorrow), helper.eauToken.totalSupply().send())
        assertEquals(initialOwnerEauBalance.plus(toBorrow), helper.eauToken.balanceOf(owner).send())
        assertEquals(toBorrow, vault.principal.send())
        val timestamp =
            helper.web3.ethGetBlockByNumber(DefaultBlockParameter.valueOf(tx.blockNumber), false).send().block.timestamp
        assertEquals(toBorrow, vault.getTotalDebt(timestamp).send())
    }

    /**
     * @given a vault is deployed by the owner
     * @when a stranger account sends borrow
     * @then error returned - operation is restricted to the owner only
     */
    @Test
    fun borrowBy3rdParty() {
        val stranger = helper.credentialsBob
        val strangerVault = Vault.load(vault.contractAddress, helper.web3, helper.credentialsBob, helper.gasProvider)
        val toBorrow = BigInteger.TEN

        assertThrows<TransactionException> {
            strangerVault.borrow(toBorrow).send()
        }
    }

    /**
     * @given MedleyDAO deployed owner has borrowed all limit
     * @when the owner borrows 50 EAU more
     * @then Error returned - credit limit exhausted
     */
    @Test
    fun borrowExceedsLimit() {
        val toBorrow = vault.creditLimit.send()
        vault.borrow(toBorrow).send()

        assertThrows<TransactionException> {
            vault.borrow(toBorrow).send()
        }
    }
}
