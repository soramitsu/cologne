package acceptance

import contract.EAUToken
import contract.UserToken
import contract.Vault
import helpers.ContractTestHelper
import org.junit.jupiter.api.BeforeEach
import org.testcontainers.containers.GenericContainer
import org.testcontainers.images.builder.ImageFromDockerfile
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.web3j.crypto.Credentials
import java.math.BigInteger
import java.nio.file.Path

/**
 * Base class for acceptance tests
 */
@Testcontainers
open class AcceptanceTest {

    @Container
    private val ganache: GenericContainer<Nothing> =
        GenericContainer<Nothing>(
            ImageFromDockerfile()
                .withDockerfile(Path.of(javaClass.getResource("/docker/ganache/Dockerfile").toURI()))
        )
            .withExposedPorts(8545)

    val initialAmount = BigInteger.valueOf(100)
    val tokenPrice = BigInteger.valueOf(2)
    lateinit var helper: ContractTestHelper
    lateinit var owner: Credentials
    lateinit var vaultByOwner: Vault

    @BeforeEach
    fun setUp() {
        helper = ContractTestHelper(ganache.host, ganache.firstMappedPort)
    }

    /**
     * Pass some time
     */
    fun passTime(period: BigInteger) {
        helper.passTime(period)
    }

    /**
     * Deploy vault with Owner credentials
     */
    fun ownerCreatesVault(initialAmount: BigInteger = this.initialAmount, tokenPrice: BigInteger = this.tokenPrice) {
        owner = helper.credentialsAlice
        val vaultAddress = helper.createVault(helper.credentialsAlice, initialAmount, tokenPrice)
        vaultByOwner = Vault.load(vaultAddress, helper.web3, owner, helper.gasProvider)
    }

    fun ownerStake(stake: BigInteger) {
        // Stake
        helper.addCLGN(owner.address, stake)
        val clgnTokenByOwner = UserToken.load(helper.clgnToken.contractAddress, helper.web3, owner, helper.gasProvider)
        clgnTokenByOwner.approve(vaultByOwner.contractAddress, stake).send()
        vaultByOwner.stake(stake).send()
    }

    fun ownerPaysOff(amount: BigInteger) {
        val eauTokenByOwner = EAUToken.load(helper.eauToken.contractAddress, helper.web3, owner, helper.gasProvider)
        eauTokenByOwner.approve(vaultByOwner.contractAddress, amount).send()
        vaultByOwner.payOff(amount).send()
    }

}
