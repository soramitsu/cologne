import helpers.ContractTestHelper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.GenericContainer
import org.testcontainers.images.builder.ImageFromDockerfile
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.nio.file.Path

/**
 * Test EAU token
 */
@Testcontainers
class InfiniteTest {

    @Container
    private val ganache: GenericContainer<Nothing> =
        GenericContainer<Nothing>(
            ImageFromDockerfile()
                .withDockerfile(Path.of(javaClass.getResource("docker/ganache/Dockerfile").toURI()))
        )
            .withExposedPorts(8545)

    lateinit var helper: ContractTestHelper
    lateinit var alice: String
    lateinit var bob: String
    lateinit var cashStash: String

    @BeforeEach
    fun setUp() {
        helper = ContractTestHelper(ganache.host, ganache.firstMappedPort)
    }

    /**
     * @given EAU Token and Alice has 20 EAU and Bob has 10 tokens and contract distribution stash is 0 EAU
     * @when Alice distributes 10 EAU tokens
     * @then Alice has 10 EAU and EAU contract has 10 EAU and dividends accrued to Alice are 5 EAU and dividends accrued
     * to Bob are 5 EAU
     */
    @Test
    fun infiniteRun() {
        println("MedleyDAO address" + helper.medleyDAO.contractAddress)
        Thread.sleep(Long.MAX_VALUE)
    }
}
