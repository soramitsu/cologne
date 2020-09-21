import contract.*
import helpers.ContractTestHelper
import helpers.VaultState
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.testcontainers.containers.GenericContainer
import org.testcontainers.images.builder.ImageFromDockerfile
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.web3j.protocol.exceptions.TransactionException
import java.math.BigInteger
import java.nio.file.Path

@Testcontainers
class VaultTest {

    @Container
    private val ganache: GenericContainer<Nothing> =
        GenericContainer<Nothing>(
            ImageFromDockerfile()
                .withDockerfile(Path.of(javaClass.getResource("/docker/ganache/Dockerfile").toURI()))
        )
            .withExposedPorts(8545)

    lateinit var helper: ContractTestHelper
    lateinit var medleyDAO: MedleyDAO
    lateinit var owner: String
    lateinit var userToken: UserToken
    lateinit var clgnToken: CLGNToken
    lateinit var eauToken: EAUToken
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
        // load CLGN token with owner credentials
        clgnToken =
            CLGNToken.load(helper.clgnToken.contractAddress, helper.web3, helper.credentialsAlice, helper.gasProvider)
        // load EAU token with owner credentials
        eauToken =
            EAUToken.load(helper.eauToken.contractAddress, helper.web3, helper.credentialsAlice, helper.gasProvider)
    }

    /**
     * Deploy vault with Owner credentials
     */
    fun ownerCreatesVault(amount: BigInteger = initialAmount, price: BigInteger = tokenPrice) {
        val vaultAddress = helper.createVault(helper.credentialsAlice, stake, amount, price)
        vault = Vault.load(vaultAddress, helper.web3, helper.credentialsAlice, helper.gasProvider)
    }

    /**
     * @given MedleyDAO deployed and no vaults created and user has 100 UserTokens
     * @when create vault called with 100 UserTokens by owner and declare price as 2 EAU/Token
     * @then new vault created and vault owner is caller and 100 UserTokens transferred to the vault and credit limit is
     * 25% of assessed value (100 TKN * 2 EAU / 4 = 50 EAU)
     */
    @Test
    fun createVault() {
        ownerCreatesVault(initialAmount, tokenPrice)

        assertEquals(VaultState.Trading.toBigInteger(), vault.state.send())
        assertEquals(owner, vault.owner().send())
        assertEquals(initialAmount, userToken.balanceOf(vault.contractAddress).send())
        assertEquals(BigInteger.ZERO, userToken.balanceOf(owner).send())
        assertEquals(tokenPrice, vault.price.send())
        assertEquals(BigInteger.valueOf(50), vault.creditLimit.send())
        assertEquals(initialAmount, vault.tokenAmount.send())
    }

    /**
     * @given owner borrowed 100'000 EAU
     * @when calculate total debt 1 year later called
     * @then 110276 EAU returned (10276 EAU accrued as interest)
     */
    @Test
    fun feesAccruedOneYearLater() {
        val initialAmount = BigInteger.valueOf(500_000)
        val tokenPrice = BigInteger.valueOf(4)
        ownerCreatesVault(initialAmount, tokenPrice)
        val toBorrow = BigInteger.valueOf(100_000)

        vault.borrow(toBorrow).send()

        assertEquals(toBorrow, vault.principal.send())
        val timestamp = helper.timeProvider.time.send()
        val oneYearLater = timestamp.plus(BigInteger.valueOf(31536000));
        helper.timeProvider.setTime(timestamp)
        assertEquals(BigInteger.valueOf(110276), vault.getTotalDebt(oneYearLater).send())
    }

    /**
     * @given Vault deployed and has user tokens, CLGN and EAU and has no debt
     * @when the owner closes vault
     * @then all assets (user tokens left in vault, EAU and CLGN stake and CLGN assets transferred from the vault to the
     * owner, vault is closed
     */
    @Test
    fun closeNoDebt() {
        ownerCreatesVault(initialAmount, tokenPrice)
        val eauBalance = BigInteger.valueOf(123)
        helper.addEAU(vault.contractAddress, eauBalance)
        val clgnBalance = BigInteger.valueOf(234)
        helper.addCLGN(vault.contractAddress, clgnBalance)

        vault.close().send()

        assertEquals(VaultState.Closed.toBigInteger(), vault.state.send())
        assertEquals(initialAmount, userToken.balanceOf(owner).send())
        assertEquals(eauBalance, eauToken.balanceOf(owner).send())
        assertEquals(clgnBalance.add(stake), clgnToken.balanceOf(owner).send())
    }

    /**
     * @given a vault owner has debt
     * @when close is called
     * @then error response - not possilbe to close vault with debt
     */
    @Test
    fun closeWithDebtNotAllowed() {
        ownerCreatesVault(initialAmount, tokenPrice)
        val toBorrow = BigInteger.TEN
        vault.borrow(toBorrow).send()

        assertEquals(VaultState.Defaulted.toBigInteger(), vault.state.send())
        assertThrows<TransactionException> {
            vault.close().send()
        }
    }

    /**
     * @given a vault and stranger account
     * @when close is called by stranger
     * @then error response - only owner allowed to close the vault
     */
    @Test
    fun closeByStranger() {
        ownerCreatesVault(initialAmount, tokenPrice)
        val stranger = helper.credentialsBob
        val strangerVault = Vault.load(vault.contractAddress, helper.web3, stranger, helper.gasProvider)

        assertThrows<TransactionException> {
            strangerVault.close().send()
        }
    }

    /**
     * @given MedleyDAO deployed owner can borrow 50 EAU
     * @when the owner borrows 50 EAU
     * @then EAU tokens are minted to the owner account, owner debt is 50 EAU
     */
    @Test
    fun borrowSunnyDay() {
        ownerCreatesVault(initialAmount, tokenPrice)
        val initialEauSupply = helper.eauToken.totalSupply().send()
        val initialOwnerEauBalance = helper.eauToken.balanceOf(owner).send()
        val toBorrow = vault.creditLimit.send()

        vault.borrow(toBorrow).send()

        assertEquals(initialEauSupply.plus(toBorrow), helper.eauToken.totalSupply().send())
        assertEquals(initialOwnerEauBalance.plus(toBorrow), helper.eauToken.balanceOf(owner).send())
        assertEquals(toBorrow, vault.principal.send())
        val timestamp = helper.timeProvider.time.send()
        assertEquals(toBorrow, vault.getTotalDebt(timestamp).send())
    }

    /**
     * @given a vault is deployed by the owner
     * @when a stranger account sends borrow
     * @then error returned - operation is restricted to the owner only
     */
    @Test
    fun borrowBy3rdParty() {
        ownerCreatesVault(initialAmount, tokenPrice)
        val stranger = helper.credentialsBob
        val strangerVault = Vault.load(vault.contractAddress, helper.web3, stranger, helper.gasProvider)
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
        ownerCreatesVault(initialAmount, tokenPrice)
        val toBorrow = vault.creditLimit.send()
        vault.borrow(toBorrow).send()

        assertThrows<TransactionException> {
            vault.borrow(toBorrow).send()
        }
    }

    /**
     * @given The vault deployed with no debt
     * @when the user pays off X EAU
     * @then the vault balance increased by X EAU
     */
    @Test
    fun payOffNoDebt() {
        ownerCreatesVault(initialAmount, tokenPrice)
        val toPayOff = BigInteger.valueOf(50_000)
        helper.addEAU(owner, toPayOff)

        eauToken.approve(vault.contractAddress, toPayOff).send()
        vault.payOff(toPayOff).send()

        assertEquals(toPayOff, eauToken.balanceOf(vault.contractAddress).send())
        assertEquals(BigInteger.ZERO, eauToken.balanceOf(owner).send())
    }

    /**
     * @given Vault is created and owner debt is 100'000 EAU and 0 fees accrued
     * @when the owner pays off 50'000 EAU
     * @then Debt is reduced to 50'000 EAU and 50'000 EAU are burnt
     */
    @Test
    fun payOffDebtPartially() {
        val initialAmount = BigInteger.valueOf(500_000)
        val tokenPrice = BigInteger.valueOf(4)
        ownerCreatesVault(initialAmount, tokenPrice)
        val debtBefore = BigInteger.valueOf(100_000)
        vault.borrow(debtBefore).send()
        val timestamp = helper.timeProvider.time.send()
        val toPayOff = BigInteger.valueOf(50_000)
        val initialEauSupply = eauToken.totalSupply().send()
        val balanceBefore = eauToken.balanceOf(owner).send()

        eauToken.approve(vault.contractAddress, toPayOff).send()
        vault.payOff(toPayOff).send()

        val newDebt = vault.getTotalDebt(timestamp).send()
        assertEquals(debtBefore.minus(toPayOff), newDebt)
        assertEquals(initialEauSupply.minus(toPayOff), eauToken.totalSupply().send())
        assertEquals(balanceBefore.minus(toPayOff), eauToken.balanceOf(owner).send())
    }
}
