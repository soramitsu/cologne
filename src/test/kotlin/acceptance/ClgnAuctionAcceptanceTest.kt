package acceptance

import contract.EAUToken
import contract.CLGNToken
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
class ClgnAuctionAcceptanceTest {

    @Container
    private val ganache: GenericContainer<Nothing> =
        GenericContainer<Nothing>(
            ImageFromDockerfile()
                .withDockerfile(Path.of(javaClass.getResource("/docker/ganache/Dockerfile").toURI()))
        )
            .withExposedPorts(8545)

    val initialAmount = BigInteger.valueOf(4000)
    val tokenPrice = BigInteger.ONE
    val initialStake = BigInteger.ZERO
    lateinit var helper: ContractTestHelper
    lateinit var owner: Credentials
    lateinit var auctionInitiator: Credentials
    lateinit var slashingInitiator: Credentials
    lateinit var coverInitiator: Credentials
    lateinit var eauToken: EAUToken
    lateinit var clgnToken: CLGNToken
    lateinit var ownerVault: Vault
    lateinit var auctionIntiatorVault: Vault
    lateinit var slashingInitiatorVault: Vault
    lateinit var coverInitiatorVault: Vault

    @BeforeEach
    fun setUp() {
        helper = ContractTestHelper(ganache.host, ganache.firstMappedPort)
        owner = helper.credentialsAlice
        auctionInitiator = helper.credentialsBob
        slashingInitiator = helper.credentialsCharlie
        coverInitiator = helper.credentialsDave
        eauToken = helper.eauToken
        clgnToken = helper.clgnToken
    }

    /**
     * Deploy vault with Owner credentials
     */
    fun ownerCreatesVault(
        amount: BigInteger = initialAmount,
        price: BigInteger = tokenPrice,
        stake: BigInteger = initialStake
    ) {
        val vaultAddress = helper.createVault(owner, amount, price)
        ownerVault = helper.vaultByOwner
        auctionIntiatorVault = Vault.load(vaultAddress, helper.web3, auctionInitiator, helper.gasProvider)
        slashingInitiatorVault = Vault.load(vaultAddress, helper.web3, slashingInitiator, helper.gasProvider)
        coverInitiatorVault = Vault.load(vaultAddress, helper.web3, coverInitiator, helper.gasProvider)
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
            coverInitiatorVault.coverShortfall().send()
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
            coverInitiatorVault.coverShortfall().send()
        }
    }

    /**
     * @given the vault is breached and the initial liquidity auction is over and not slashed
     * @when cover shortfall called
     * @then error returned - should be slashed
     */
    @Test
    fun coverShortfallNotSlashed() {
        ownerCreatesVault(stake = BigInteger.TEN)
        breachVault()
        failInitialAuction()

        assertThrows<TransactionException> {
            coverInitiatorVault.coverShortfall().send()
        }
    }

    /**
     * @given the vault is breached and the initial liquidity auction is over and caller doesn't have CLGN
     * @when cover shortfall called
     * @then error returned - CLGN holder with at least 5% of total remaining outstanding EAU notional in the defaulted
     * Vault can initiate a CLGN mint
     */
    @Test
    fun coverShortfallWrongInitiator() {
        ownerCreatesVault()
        breachVault()
        failInitialAuction()

        assertThrows<TransactionException> {
            coverInitiatorVault.coverShortfall().send()
        }
    }

    /**
     * @given the vault is slashed and has debt of 1000 EAU and cover initiator has 25 CLGN (assessed as 50 EAU) which
     * is 5% of outstanding debt
     * @when cover shortfall called
     * @then 550 CLGN minted and sold for 1100 EAU, 1000 EAU are paid off and 100 EAU paid to initiator as bounty
     */
    @Test
    fun coverShortfall() {
        helper.addEAU(helper.marketAdaptor.contractAddress, BigInteger.valueOf(1100))
        helper.addCLGN(coverInitiator.address, BigInteger.valueOf(25))
        ownerCreatesVault()
        breachVault()
        failInitialAuction()
        assertEquals(BigInteger.ZERO, ownerVault.fees.send())
        assertEquals(BigInteger.valueOf(1000), ownerVault.principal.send())
        val initialClgnSupply = clgnToken.totalSupply().send()
        assertEquals(VaultState.WaitingForSlashing.toBigInteger(), ownerVault.state.send())
        slashingInitiatorVault.slash().send()
        assertEquals(VaultState.WaitingForClgnAuction.toBigInteger(), ownerVault.state.send())

        coverInitiatorVault.coverShortfall().send()

        assertEquals(VaultState.Slashed.toBigInteger(), ownerVault.state.send())
        assertEquals(BigInteger.valueOf(550), clgnToken.totalSupply().send().subtract(initialClgnSupply))
        assertEquals(BigInteger.ZERO, ownerVault.getTotalDebt().send())
        assertEquals(BigInteger.valueOf(100), eauToken.balanceOf(coverInitiator.address).send())
        assertEquals(VaultState.Slashed.toBigInteger(), ownerVault.state.send())
    }
}
