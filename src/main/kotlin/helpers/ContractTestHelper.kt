package helpers

import contract.EAUToken
import contract.MDLYToken
import contract.MarketAdaptorMock
import contract.PriceOracleMock
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
    var web3: Web3j = Web3j.build(
        HttpService(
            HttpUrl.Builder().scheme("http").host(host).port(port).build().toString()
        )
    )
    val gasProvider = StaticGasProvider(BigInteger.valueOf(150_000_000_000), BigInteger.valueOf(2_500_000))

    // Ethereum wallets
    val credentialsAlice = Credentials.create("0x2222222222222222222222222222222222222222222222222222222222222222")
    var credentialsBob = Credentials.create("0x3333333333333333333333333333333333333333333333333333333333333333")

    // Contracts
    var mdlyToken: MDLYToken
    var eauToken: EAUToken
    var priceOracle: PriceOracleMock
    var marketAdaptor: MarketAdaptorMock

    init {
        // Deploy contracts
        val seedPrivateKey = "0x1111111111111111111111111111111111111111111111111111111111111111"
        val seed = Credentials.create(seedPrivateKey)
        mdlyToken = MDLYToken.deploy(web3, seed, gasProvider).send()
        eauToken = EAUToken.deploy(web3, seed, gasProvider).send()
        priceOracle =
            PriceOracleMock.deploy(web3, seed, gasProvider, mdlyToken.contractAddress, eauToken.contractAddress).send()
        marketAdaptor =
            MarketAdaptorMock.deploy(web3, seed, gasProvider, mdlyToken.contractAddress, eauToken.contractAddress)
                .send()
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
}
