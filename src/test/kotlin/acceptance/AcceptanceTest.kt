package acceptance

import contract.*
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

    val initialAmount = toTokenAmount(1_000)
    val tokenPrice = toTokenAmount(4)
    lateinit var helper: ContractTestHelper
    lateinit var owner: Credentials
    lateinit var buyer: Credentials
    // Initial Liquidity Auction Initiator
    lateinit var initiator: Credentials
    lateinit var slasher: Credentials

    lateinit var vaultByOwner: Vault
    lateinit var vaultByBuyer: Vault
    lateinit var vaultByInitiator: Vault
    lateinit var vaultBySlasher: Vault
    lateinit var eauToken: EAUToken
    lateinit var clgnToken: CLGNToken
    lateinit var eauTokenByBuyer: EAUToken
    lateinit var userToken: UserToken

    @BeforeEach
    fun setUp() {
        helper = ContractTestHelper(ganache.host, ganache.firstMappedPort)

        owner = helper.credentialsAlice
        buyer = helper.credentialsBob
        initiator = helper.credentialsCharlie
        slasher = helper.credentialsDave

        userToken = helper.userToken
        eauToken = helper.eauToken
        clgnToken = helper.clgnToken

        eauTokenByBuyer = EAUToken.load(helper.eauToken.contractAddress, helper.web3, buyer, helper.gasProvider)
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
        val vaultAddress = helper.createVault(owner, initialAmount, tokenPrice)
        vaultByOwner = Vault.load(vaultAddress, helper.web3, owner, helper.gasProvider)
        vaultByBuyer = Vault.load(vaultAddress, helper.web3, buyer, helper.gasProvider)
        vaultByInitiator = Vault.load(vaultAddress, helper.web3, initiator, helper.gasProvider)
        vaultBySlasher = Vault.load(vaultAddress, helper.web3, slasher, helper.gasProvider)
    }

    /**
     * Stakes CLGN to the owner vault
     * @param stake in attoCLGN
     */
    fun ownerStake(stake: BigInteger) {
        // Stake
        helper.addCLGN(owner.address, stake)
        val clgnTokenByOwner = UserToken.load(helper.clgnToken.contractAddress, helper.web3, owner, helper.gasProvider)
        clgnTokenByOwner.approve(vaultByOwner.contractAddress, stake).send()
        vaultByOwner.stake(stake).send()
    }

    /**
     * @param amount to pay off in attoEAU
     */
    fun ownerPaysOff(amount: BigInteger) {
        val eauTokenByOwner = EAUToken.load(helper.eauToken.contractAddress, helper.web3, owner, helper.gasProvider)
        eauTokenByOwner.approve(vaultByOwner.contractAddress, amount).send()
        vaultByOwner.payOff(amount).send()
    }

    /**
     * Breach credit limit
     */
    fun ownerBreachesVault() {
        val toBorrow = vaultByOwner.canBorrow().send()
        vaultByOwner.borrow(toBorrow).send()
    }

    fun toTokenAmount(amount: Long): BigInteger {
        return ContractTestHelper.toTokenAmount(amount)
    }

    fun toPrettyBalance(balance: BigInteger): String {
        return balance.div(BigInteger.TEN.pow(18)).toString()
    }

    fun getEauToBuyUserTokenAmount(amount: BigInteger, tokenPrice: BigInteger = this.tokenPrice): BigInteger {
        return amount.multiply(tokenPrice).divide(BigInteger.TEN.pow(userToken.decimals().send().toInt()))
    }

    /**
     * Fail initial liquidity auction
     */
    fun failInitialAuction() {
        // Fail Initial Liquidity auction - Dutch auction has passed
        val time = helper.timeProvider.time.send().add(BigInteger.valueOf(180000))
        helper.timeProvider.setTime(time).send()
    }
}
