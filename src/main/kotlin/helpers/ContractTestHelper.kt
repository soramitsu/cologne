package helpers

import contract.*
import okhttp3.HttpUrl
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.tx.gas.StaticGasProvider
import java.math.BigInteger

/**
 * Creates credentials and deploy contracts
 */
class ContractTestHelper(host: String, port: Int) {
    val web3: Web3j = Web3j.build(
        HttpService(
            HttpUrl.Builder().scheme("http").host(host).port(port).build().toString()
        )
    )
    val gasProvider = StaticGasProvider(BigInteger.valueOf(150_000_000_000), BigInteger.valueOf(4_500_000))

    // Ethereum wallets
    val credentialsSeed = Credentials.create("0x1111111111111111111111111111111111111111111111111111111111111111")
    val credentialsAlice = Credentials.create("0x2222222222222222222222222222222222222222222222222222222222222222")
    val credentialsBob = Credentials.create("0x3333333333333333333333333333333333333333333333333333333333333333")
    val credentialsCharlie = Credentials.create("0x4444444444444444444444444444444444444444444444444444444444444444")
    val credentialsDave = Credentials.create("0x5555555555555555555555555555555555555555555555555555555555555555")

    // Contracts
    val clgnToken: CLGNToken
    val eauToken: EAUToken
    val userToken: UserToken
    val priceOracle: PriceOracleMock
    val marketAdaptor: MarketAdaptorMock
    val medleyDAO: MedleyDAO
    lateinit var vaultByOwner: Vault
    val timeProvider: TimeProviderMock

    // CLGN/EAU exchange rate
    val clgnEauPrice = BigInteger.TWO

    init {
        // Deploy contracts
        val seed = Credentials.create("0x1111111111111111111111111111111111111111111111111111111111111111")
        clgnToken = CLGNToken.deploy(web3, seed, gasProvider).send()
        eauToken = EAUToken.deploy(web3, seed, gasProvider).send()
        userToken = UserToken.deploy(web3, seed, gasProvider).send()
        priceOracle =
            PriceOracleMock.deploy(web3, seed, gasProvider, clgnToken.contractAddress, eauToken.contractAddress).send()
        marketAdaptor =
            MarketAdaptorMock.deploy(web3, seed, gasProvider, clgnToken.contractAddress, eauToken.contractAddress)
                .send()
        timeProvider = TimeProviderMock.deploy(web3, seed, gasProvider).send()
        medleyDAO = MedleyDAO.deploy(
            web3,
            seed,
            gasProvider,
            clgnToken.contractAddress,
            eauToken.contractAddress,
            priceOracle.contractAddress,
            marketAdaptor.contractAddress,
            timeProvider.contractAddress
        ).send()
        clgnToken.transferOwnership(medleyDAO.contractAddress).send()
    }

    fun addCLGN(address: String, amount: BigInteger) {
        clgnToken.transfer(address, amount).send()
    }

    fun addEAU(address: String, amount: BigInteger) {
        eauToken.mint(address, amount).send()
    }

    fun distributeEAU(address: String, amount: BigInteger) {
        if (address == credentialsAlice.address) {
            val aliceEau = EAUToken.load(eauToken.contractAddress, web3, credentialsAlice, gasProvider)
            aliceEau.distribute(amount).send()
        } else if (address == credentialsBob.address) {
            val bobEau = EAUToken.load(eauToken.contractAddress, web3, credentialsBob, gasProvider)
            bobEau.distribute(amount).send()
        } else {
            throw IllegalAccessException("Wrong address - don't know credentials")
        }
    }

    /**
     * Creates vault with owner provided by credentials
     * @param owner - the owner of vault
     * @param stakeAmount - stake in CLGN
     * @param userTokenAmount - amount of user tokens
     * @param userTokenPrice - price of user tokens in EAU
     * @return vault address
     */
    fun createVault(
        owner: Credentials,
        stakeAmount: BigInteger,
        userTokenAmount: BigInteger,
        userTokenPrice: BigInteger
    ): String {
        userToken.mint(owner.address, userTokenAmount).send()
        // User token by credentials
        val tokenByOwner = UserToken.load(userToken.contractAddress, web3, owner, gasProvider)
        tokenByOwner.approve(medleyDAO.contractAddress, userTokenAmount).send()

        clgnToken.transfer(owner.address, stakeAmount).send()
        val clgnTokenByOwner = UserToken.load(clgnToken.contractAddress, web3, owner, gasProvider)
        clgnTokenByOwner.approve(medleyDAO.contractAddress, stakeAmount).send()

        val medleyDaoByOwner = MedleyDAO.load(medleyDAO.contractAddress, web3, owner, gasProvider)
        val tx =
            medleyDaoByOwner.createVault(userToken.contractAddress, stakeAmount, userTokenAmount, userTokenPrice).send()
        val vaultAddress = medleyDaoByOwner.getVaultCreationEvents(tx).last().vault

        vaultByOwner = Vault.load(vaultAddress, web3, owner, gasProvider)

        return vaultAddress
    }

    /**
     * Set new time as current + period
     */
    fun passTime(period: BigInteger) {
        timeProvider.setTime(timeProvider.time.send().add(period)).send()
    }

    /**
     * Breach credit limit
     */
    fun breachVault() {
        val toBorrow = vaultByOwner.creditLimit.send()
        vaultByOwner.borrow(toBorrow).send()
    }
}
