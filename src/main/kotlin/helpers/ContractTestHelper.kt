package helpers

import contract.*
import okhttp3.HttpUrl
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.tx.gas.StaticGasProvider
import java.math.BigInteger

/**
 * Vault states
 */
enum class VaultState {
    Trading, Defaulted, InitialLiquidityAuctionInProcess, WaitingForSlashing, WaitingForClgnAuction, Slashed, Closed, SoldOut;

    fun toBigInteger(): BigInteger {
        return BigInteger.valueOf(this.ordinal.toLong())
    }
}

/**
 * Creates credentials and deploy contracts
 */
class ContractTestHelper(host: String, port: Int) {
    val web3: Web3j = Web3j.build(
        HttpService(
            HttpUrl.Builder().scheme("http").host(host).port(port).build().toString()
        )
    )
    val gasProvider = StaticGasProvider(BigInteger.valueOf(150_000_000_000), BigInteger.valueOf(6_000_000))

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
    val marketAdaptor: MarketAdaptorMock
    val cologneDAO: CologneDAO
    lateinit var vaultByOwner: Vault
    val timeProvider: TimeProviderMock

    // CLGN/EAU exchange rate
    val clgnEauPrice = BigInteger.TWO

    init {
        // Deploy contracts
        clgnToken = CLGNToken.deploy(web3, credentialsSeed, gasProvider).send()
        eauToken = EAUToken.deploy(web3, credentialsSeed, gasProvider).send()
        userToken = UserToken.deploy(web3, credentialsSeed, gasProvider).send()
        marketAdaptor =
            MarketAdaptorMock.deploy(
                web3,
                credentialsSeed,
                gasProvider,
                clgnToken.contractAddress,
                eauToken.contractAddress
            ).send()
        timeProvider = TimeProviderMock.deploy(web3, credentialsSeed, gasProvider).send()
        cologneDAO = CologneDAO.deploy(
            web3,
            credentialsSeed,
            gasProvider,
            clgnToken.contractAddress,
            eauToken.contractAddress,
            marketAdaptor.contractAddress,
            timeProvider.contractAddress
        ).send()
        clgnToken.transferOwnership(cologneDAO.contractAddress).send()
        // some EAU for tests
        eauToken.mint(credentialsSeed.address, toTokenAmount(100_000_000)).send()
        eauToken.transferOwnership(cologneDAO.contractAddress).send()

        // add liquidity to market
        eauToken.transfer(marketAdaptor.contractAddress, toTokenAmount(1_000_000)).send()
        clgnToken.transfer(marketAdaptor.contractAddress, toTokenAmount(1_000_000)).send()
    }

    fun addCLGN(address: String, amount: BigInteger) {
        clgnToken.transfer(address, amount).send()
    }

    fun addEAU(address: String, amount: BigInteger) {
        eauToken.transfer(address, amount).send()
    }

    fun addAndApproveEAU(account: Credentials, spender: String, amount: BigInteger) {
        addEAU(account.address, amount)
        val eauTokenByAccount = EAUToken.load(eauToken.contractAddress, web3, account, gasProvider)
        eauTokenByAccount.approve(spender, amount).send()
    }

    /**
     * Creates vault with owner provided by credentials
     * @param owner - the owner of vault
     * @param userTokenAmount - amount of user tokens
     * @param userTokenPrice - price of user tokens in EAU
     * @return vault address
     */
    fun createVault(
        owner: Credentials,
        userTokenAmount: BigInteger,
        userTokenPrice: BigInteger
    ): String {
        userToken.mint(owner.address, userTokenAmount).send()
        // User token by credentials
        val tokenByOwner = UserToken.load(userToken.contractAddress, web3, owner, gasProvider)
        tokenByOwner.approve(cologneDAO.contractAddress, userTokenAmount).send()

        val cologneDaoByOwner = CologneDAO.load(cologneDAO.contractAddress, web3, owner, gasProvider)
        val tx =
            cologneDaoByOwner.createVault(userToken.contractAddress, userTokenAmount, userTokenPrice).send()
        val vaultAddress = cologneDaoByOwner.getVaultCreationEvents(tx).last().vault

        vaultByOwner = Vault.load(vaultAddress, web3, owner, gasProvider)

        return vaultAddress
    }

    fun stake(vaultAddress: String, owner: Credentials, amount: BigInteger) {
        clgnToken.transfer(owner.address, amount).send()
        val clgnTokenByOwner = UserToken.load(clgnToken.contractAddress, web3, owner, gasProvider)
        clgnTokenByOwner.approve(cologneDAO.contractAddress, amount).send()
        vaultByOwner = Vault.load(vaultAddress, web3, owner, gasProvider)
        vaultByOwner.stake(amount).send()
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
        val toBorrow = vaultByOwner.canBorrow().send()
        vaultByOwner.borrow(toBorrow).send()
    }

    fun getBalances(account: String): Triple<BigInteger, BigInteger, BigInteger> {
        val tkns = userToken.balanceOf(account).send()
        val eaus = eauToken.balanceOf(account).send()
        val clgns = clgnToken.balanceOf(account).send()
        return Triple(tkns, eaus, clgns)
    }

    companion object {
        fun toTokenAmount(amount: Long): BigInteger {
            return BigInteger.valueOf(amount).multiply(BigInteger.TEN.pow(18));
        }
    }
}
