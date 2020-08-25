import contract.MedleyDAO
import contract.Vault
import helpers.ContractTestHelper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.GenericContainer
import org.testcontainers.images.builder.ImageFromDockerfile
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.math.BigInteger
import java.nio.file.Path

@Testcontainers
class MedleyDaoTest {

    @Container
    private val ganache: GenericContainer<Nothing> =
        GenericContainer<Nothing>(
            ImageFromDockerfile()
                .withDockerfile(Path.of(javaClass.getResource("docker/ganache/Dockerfile").toURI()))
        )
            .withExposedPorts(8545)

    lateinit var helper: ContractTestHelper
    lateinit var medleyDAO: MedleyDAO

    @BeforeEach
    fun setUp() {
        helper = ContractTestHelper(ganache.host, ganache.firstMappedPort)
        medleyDAO = helper.medleyDAO
    }

    /**
     * @given MedleyDAO deployed and no vaults created
     * @when create vault called
     * @then new vault created and stored in MedleyDAO vault list
     */
    @Test
    fun deployVault() {
        assertEquals(0, medleyDAO.listVaults().send().size)

        val vault = helper.createVault(helper.credentialsAlice, BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO)

        val vaults = medleyDAO.listVaults().send()
        assertEquals(1, vaults.size)
        assertEquals(vault, vaults.last())
    }
}
