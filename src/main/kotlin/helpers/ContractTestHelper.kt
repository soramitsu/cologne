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
    val credentialsAlice = Credentials.create("0x2222222222222222222222222222222222222222222222222222222222222222")
    val credentialsBob = Credentials.create("0x3333333333333333333333333333333333333333333333333333333333333333")

    // Contracts
    val mdlyToken: MDLYToken
    val eauToken: EAUToken
    val userToken: UserToken
    val priceOracle: PriceOracleMock
    val marketAdaptor: MarketAdaptorMock
    val medleyDAO: MedleyDAO
    val timeProvider: TimeProviderMock

    init {
        // Deploy contracts
        val seed = Credentials.create("0x1111111111111111111111111111111111111111111111111111111111111111")
        mdlyToken = MDLYToken.deploy(web3, seed, gasProvider).send()
        eauToken = EAUToken.deploy(web3, seed, gasProvider).send()
        userToken = UserToken.deploy(web3, seed, gasProvider).send()
        priceOracle =
            PriceOracleMock.deploy(web3, seed, gasProvider, mdlyToken.contractAddress, eauToken.contractAddress).send()
        marketAdaptor =
            MarketAdaptorMock.deploy(web3, seed, gasProvider, mdlyToken.contractAddress, eauToken.contractAddress)
                .send()
        timeProvider = TimeProviderMock.deploy(web3, seed, gasProvider).send()
        medleyDAO = MedleyDAO.deploy(
            web3,
            seed,
            gasProvider,
            mdlyToken.contractAddress,
            eauToken.contractAddress,
            priceOracle.contractAddress,
            marketAdaptor.contractAddress,
            timeProvider.contractAddress
        ).send()
    }

    fun addMDLY(address: String, amount: BigInteger) {
        mdlyToken.mint(address, amount).send()
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
     * @param stakeAmount - stake in MDLY
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

        mdlyToken.mint(owner.address, stakeAmount).send()
        val mdlyTokenByOwner = UserToken.load(mdlyToken.contractAddress, web3, owner, gasProvider)
        mdlyTokenByOwner.approve(medleyDAO.contractAddress, stakeAmount).send()

        val medleyDaoByOwner = MedleyDAO.load(medleyDAO.contractAddress, web3, owner, gasProvider)
        val tx =
            medleyDaoByOwner.createVault(userToken.contractAddress, stakeAmount, userTokenAmount, userTokenPrice).send()
        return medleyDaoByOwner.getVaultCreationEvents(tx).last().vault
    }

    /**
     * Set new time as current + period
     */
    fun passTime(period: BigInteger) {
        timeProvider.setTime(timeProvider.time.send().add(period)).send()
    }
}
