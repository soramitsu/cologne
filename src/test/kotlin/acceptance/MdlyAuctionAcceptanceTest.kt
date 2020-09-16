package acceptance

import contract.EAUToken
import contract.MDLYToken
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

@Testcontainers
class MdlyAuctionAcceptanceTest {

    @Container
    private val ganache: GenericContainer<Nothing> =
        GenericContainer<Nothing>(
            ImageFromDockerfile()
                .withDockerfile(Path.of(javaClass.getResource("/docker/ganache/Dockerfile").toURI()))
        )
            .withExposedPorts(8545)

    val initialAmount = BigInteger.valueOf(1000)
    val tokenPrice = BigInteger.valueOf(2)
    val initialStake = BigInteger.valueOf(100)
    lateinit var helper: ContractTestHelper
    lateinit var owner: Credentials
    lateinit var auctionInitiator: Credentials
    lateinit var slashingIntiator: Credentials
    lateinit var eauToken: EAUToken
    lateinit var mdlyToken: MDLYToken
    lateinit var ownerVault: Vault
    lateinit var auctionIntiatorVault: Vault
    lateinit var slashingInitiatorVault: Vault

    @BeforeEach
    fun setUp() {
        helper = ContractTestHelper(ganache.host, ganache.firstMappedPort)
        owner = helper.credentialsAlice
        auctionInitiator = helper.credentialsBob
        slashingIntiator = helper.credentialsCharlie
        eauToken = helper.eauToken
        mdlyToken = helper.mdlyToken
    }

    /**
     * Deploy vault with Owner credentials
     */
    fun ownerCreatesVault(
        amount: BigInteger = initialAmount,
        price: BigInteger = tokenPrice,
        stake: BigInteger = initialStake
    ) {
        val vaultAddress = helper.createVault(owner, stake, amount, price)
        ownerVault = helper.vaultByOwner
        auctionIntiatorVault = Vault.load(ownerVault.contractAddress, helper.web3, auctionInitiator, helper.gasProvider)
        slashingInitiatorVault =
            Vault.load(ownerVault.contractAddress, helper.web3, slashingIntiator, helper.gasProvider)
    }

    /**
     * Breach credit limit
     */
    fun breachVault() {
        helper.breachVault()
    }

    fun failInitialAuction() {
        auctionIntiatorVault.startInitialLiquidityAuction().send()
        // Dutch auction has passed
        val time = helper.timeProvider.time.send().add(BigInteger.valueOf(180000))
        helper.timeProvider.setTime(time).send()

    }

    /**
     * @given closed vault
     * @when cover shortfall called
     * @then error returned - cannot call closed vault
     */
    @Test
    fun coverShortfallClose() {
        ownerCreatesVault()
        ownerVault.close().send()

        assertThrows<TransactionException> {
            ownerVault.coverShortfall().send()
        }
    }

    /**
     * @given the vault is breached and auction has not been over yet
     * @when cover shortfall called
     * @then error returned - cannot call closed vault
     */
    @Test
    fun coverShortfallAuction() {
        ownerCreatesVault()
        breachVault()
        auctionIntiatorVault.startInitialLiquidityAuction().send()

        assertThrows<TransactionException> {
            ownerVault.coverShortfall().send()
        }
    }

    // TODO call invalid caller
    // sunny day

}
