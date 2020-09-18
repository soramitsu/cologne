import contract.PriceOracleMock
import org.testcontainers.junit.jupiter.Testcontainers
import helpers.ContractTestHelper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.GenericContainer
import org.testcontainers.images.builder.ImageFromDockerfile
import org.testcontainers.junit.jupiter.Container
import java.math.BigInteger
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertEquals

/**
 * Test EAU token
 */
@Testcontainers
class PriceOracleTest {

    @Container
    private val ganache: GenericContainer<Nothing> =
        GenericContainer<Nothing>(
            ImageFromDockerfile()
                .withDockerfile(Path.of(javaClass.getResource("docker/ganache/Dockerfile").toURI()))
        )
            .withExposedPorts(8545)

    lateinit var helper: ContractTestHelper
    lateinit var priceOracle: PriceOracleMock
    lateinit var clgn: String
    lateinit var eau: String

    @BeforeEach
    fun setUp() {
        helper = ContractTestHelper(ganache.host, ganache.firstMappedPort)
        priceOracle = helper.priceOracle
        clgn = helper.clgnToken.contractAddress
        eau = helper.eauToken.contractAddress
    }

    /**
     * @given Price oracle with rate CLGN/EAU = 2
     * @when get assessed value for 10 CLGN
     * @then 20 EAU returned
     */
    @Test
    fun testClgnToEauRate() {
        assertEquals(BigInteger.valueOf(20), priceOracle.consult(clgn, BigInteger.TEN).send())
    }

    /**
     * @given Price oracle with rate CLGN/EAU = 2
     * @when get assessed value for 10 EAU
     * @then 5 CLGN returned
     */
    @Test
    fun testEauToClgnRate() {
        assertEquals(BigInteger.valueOf(5), priceOracle.consult(eau, BigInteger.TEN).send())
    }

}
