import helpers.ContractTestHelper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
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

    @BeforeEach
    fun setUp() {
        helper = ContractTestHelper(ganache.host, ganache.firstMappedPort)
    }

    /**
     * Run testcontainer and deploy contracts
     */
    @Test
//    @Disabled
    fun infiniteRun() {
        println("MedleyDAO address: " + helper.medleyDAO.contractAddress)
        Thread.sleep(Long.MAX_VALUE)
    }
}
