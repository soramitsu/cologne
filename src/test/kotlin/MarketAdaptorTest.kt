import contract.EAUToken
import contract.MarketAdaptorMock
import helpers.ContractTestHelper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.GenericContainer
import org.testcontainers.images.builder.ImageFromDockerfile
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.web3j.protocol.exceptions.TransactionException
import java.math.BigInteger
import java.nio.file.Path

@Testcontainers
class MarketAdaptorTest {

    @Container
    private val ganache: GenericContainer<Nothing> =
        GenericContainer<Nothing>(
            ImageFromDockerfile()
                .withDockerfile(Path.of(javaClass.getResource("docker/ganache/Dockerfile").toURI()))
        )
            .withExposedPorts(8545)

    lateinit var helper: ContractTestHelper
    lateinit var buyer: String
    lateinit var market: MarketAdaptorMock
    lateinit var clgn: String
    lateinit var eau: String
    lateinit var clgn2Eau: List<String>
    lateinit var eau2Clgn: List<String>

    // deadline not used in mock market
    val deadline = BigInteger.ZERO

    @BeforeEach
    fun setUp() {
        helper = ContractTestHelper(ganache.host, ganache.firstMappedPort)
        buyer = helper.credentialsAlice.address
        market = helper.marketAdaptor
        clgn = helper.clgnToken.contractAddress
        eau = helper.eauToken.contractAddress
        clgn2Eau = listOf(clgn, eau)
        eau2Clgn = listOf(eau, clgn)
    }

    fun buyerAllows(token: String, spender: String, amount: BigInteger) {
        val aliceEau = EAUToken.load(token, helper.web3, helper.credentialsAlice, helper.gasProvider)
        aliceEau.approve(spender, amount).send()
    }

    /**
     * @given Market has 10 EAU
     * @when Buyer ask amount in EAU for 5 CLGN
     * @then Market response for 5 CLGN is 10 EAU
     */
    @Test
    fun getAmountsOutClgn2EauTest() {
        helper.addEAU(market.contractAddress, BigInteger.TEN)

        val actual = market.getAmountsOut(BigInteger.valueOf(5), clgn2Eau).send()

        assertEquals(2, actual.size)
        assertEquals(BigInteger.valueOf(5), actual[0])
        assertEquals(BigInteger.TEN, actual[1])
    }

    /**
     * @given Market has 10 EAU
     * @when Buyer ask amount in CLGN to get 10 EAU
     * @then Market response for 5 CLGN is 10 EAU
     */
    @Test
    fun getAmountsInClgn2EauTest() {
        helper.addEAU(market.contractAddress, BigInteger.TEN)

        val actual = market.getAmountsIn(BigInteger.TEN, clgn2Eau).send()

        assertEquals(2, actual.size)
        assertEquals(BigInteger.valueOf(5), actual[0])
        assertEquals(BigInteger.TEN, actual[1])
    }

    /**
     * @given Market has 5 CLGN
     * @when Buyer ask amount in CLGN for 10 EAU
     * @then Market response for 10 EAU is 5 CLGN
     */
    @Test
    fun getAmountsOutEau2ClgnTest() {
        helper.addCLGN(market.contractAddress, BigInteger.valueOf(5))

        val actual = market.getAmountsOut(BigInteger.TEN, eau2Clgn).send()

        assertEquals(2, actual.size)
        assertEquals(BigInteger.TEN, actual[0])
        assertEquals(BigInteger.valueOf(5), actual[1])
    }

    /**
     * @given Market has 5 CLGN
     * @when Buyer ask amount in EAU to get 5 CLGN
     * @then Market response for 10 EAU for 5 CLGN
     */
    @Test
    fun getAmountsInEau2ClgnTest() {
        helper.addCLGN(market.contractAddress, BigInteger.valueOf(5))

        val actual = market.getAmountsIn(BigInteger.valueOf(5), eau2Clgn).send()

        assertEquals(2, actual.size)
        assertEquals(BigInteger.TEN, actual[0])
        assertEquals(BigInteger.valueOf(5), actual[1])
    }

    /**
     * @given Market has 10 EAU and buyer has 5 CLGN
     * @when buyer swaps exactly 5 CLGN for at least 9 EAU
     * @then swap executed, market has 5 CLGN and 0 EAU and buyer has 0 CLGN and 10 EAU
     */
    @Test
    fun swapExactTokensForTokensClgn2EauSunnyDay() {
        helper.addEAU(market.contractAddress, BigInteger.TEN)
        helper.addCLGN(buyer, BigInteger.valueOf(5))
        buyerAllows(clgn, market.contractAddress, BigInteger.valueOf(5))

        market.swapExactTokensForTokens(BigInteger.valueOf(5), BigInteger.valueOf(9), clgn2Eau, buyer, deadline)
            .send()

        assertEquals(BigInteger.valueOf(5), helper.clgnToken.balanceOf(market.contractAddress).send())
        assertEquals(BigInteger.ZERO, helper.eauToken.balanceOf(market.contractAddress).send())
        assertEquals(BigInteger.ZERO, helper.clgnToken.balanceOf(buyer).send())
        assertEquals(BigInteger.TEN, helper.eauToken.balanceOf(buyer).send())
    }

    /**
     * @given Market has 10 EAU and buyer has 5 CLGN
     * @when buyer swaps exactly 5 CLGN for at least 11 EAU
     * @then swap executed, market has 5 CLGN and 0 EAU and buyer has 0 CLGN and 10 EAU
     */
    @Test
    fun swapExactTokensForTokensClgn2EauTooMuchExpect() {
        helper.addEAU(market.contractAddress, BigInteger.TEN)
        helper.addCLGN(buyer, BigInteger.valueOf(5))
        buyerAllows(clgn, market.contractAddress, BigInteger.valueOf(5))

        assertThrows<TransactionException> {
            market.swapExactTokensForTokens(BigInteger.valueOf(5), BigInteger.valueOf(11), clgn2Eau, buyer, deadline)
                .send()
        }
    }

    /**
     * @given Market has 5 CLGN and buyer has 10 EAU
     * @when buyer swaps exactly 10 EAU tokens for at least 4 CLGN
     * @then swap executed, market has 0 CLGN and 10 EAU and buyer has 5 CLGN and 0 EAU
     */
    @Test
    fun swapExactTokensForTokensEau2ClgnSunnyDay() {
        helper.addCLGN(market.contractAddress, BigInteger.valueOf(5))
        helper.addEAU(buyer, BigInteger.TEN)
        buyerAllows(eau, market.contractAddress, BigInteger.TEN)

        market.swapExactTokensForTokens(BigInteger.TEN, BigInteger.valueOf(4), eau2Clgn, buyer, deadline).send()

        assertEquals(BigInteger.ZERO, helper.clgnToken.balanceOf(market.contractAddress).send())
        assertEquals(BigInteger.TEN, helper.eauToken.balanceOf(market.contractAddress).send())
        assertEquals(BigInteger.valueOf(5), helper.clgnToken.balanceOf(buyer).send())
        assertEquals(BigInteger.ZERO, helper.eauToken.balanceOf(buyer).send())
    }

    /**
     * @given Market has 10 EAU and buyer has 5 CLGN
     * @when buyer swaps at least 6 CLGN for exactly 10 EAU
     * @then swap executed, market has 5 CLGN and 0 EAU and buyer has 1 CLGN and 10 EAU
     */
    @Test
    fun swapTokensForExactTokensClgn2EauSunnyDay() {
        helper.addEAU(market.contractAddress, BigInteger.TEN)
        helper.addCLGN(buyer, BigInteger.valueOf(6))
        buyerAllows(clgn, market.contractAddress, BigInteger.valueOf(6))

        // get exactly 10 EAU and spend up to 6 CLGN
        market.swapTokensForExactTokens(BigInteger.TEN, BigInteger.valueOf(6), clgn2Eau, buyer, deadline)
            .send()

        assertEquals(BigInteger.valueOf(5), helper.clgnToken.balanceOf(market.contractAddress).send())
        assertEquals(BigInteger.ZERO, helper.eauToken.balanceOf(market.contractAddress).send())
        assertEquals(BigInteger.ONE, helper.clgnToken.balanceOf(buyer).send())
        assertEquals(BigInteger.TEN, helper.eauToken.balanceOf(buyer).send())
        // now it is responsibility of a buyer to remove allowance on leftover
        assertEquals(BigInteger.ONE, helper.clgnToken.allowance(buyer, market.contractAddress).send())
    }

    /**
     * @given Market has 5 CLGN and buyer has 12 EAU
     * @when buyer swaps at least 12 EAU for exactly 5 CLGN
     * @then swap executed, market has 0 CLGN and 10 EAU and buyer has 5 CLGN and 2 EAU
     */
    @Test
    fun swapTokensForExactTokensEau2ClgnSunnyDay() {
        helper.addCLGN(market.contractAddress, BigInteger.valueOf(5))
        helper.addEAU(buyer, BigInteger.valueOf(12))
        buyerAllows(eau, market.contractAddress, BigInteger.valueOf(12))

        // get exactly 5 CLGN and spend up to 12 CLGN
        market.swapTokensForExactTokens(BigInteger.valueOf(5), BigInteger.valueOf(12), eau2Clgn, buyer, deadline)
            .send()

        assertEquals(BigInteger.ZERO, helper.clgnToken.balanceOf(market.contractAddress).send())
        assertEquals(BigInteger.TEN, helper.eauToken.balanceOf(market.contractAddress).send())
        assertEquals(BigInteger.valueOf(5), helper.clgnToken.balanceOf(buyer).send())
        assertEquals(BigInteger.TWO, helper.eauToken.balanceOf(buyer).send())
        // now it is responsibility of a buyer to remove allowance on leftover
        assertEquals(BigInteger.TWO, helper.eauToken.allowance(buyer, market.contractAddress).send())
    }
}
