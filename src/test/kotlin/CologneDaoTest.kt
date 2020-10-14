import contract.CologneDAO
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
class CologneDaoTest {

    @Container
    private val ganache: GenericContainer<Nothing> =
        GenericContainer<Nothing>(
            ImageFromDockerfile()
                .withDockerfile(Path.of(javaClass.getResource("docker/ganache/Dockerfile").toURI()))
        )
            .withExposedPorts(8545)

    lateinit var helper: ContractTestHelper
    lateinit var cologneDAO: CologneDAO

    @BeforeEach
    fun setUp() {
        helper = ContractTestHelper(ganache.host, ganache.firstMappedPort)
        cologneDAO = helper.cologneDAO
    }

    /**
     * @given CologneDAO deployed and no vaults created
     * @when create vault called by the User
     * @then new vault created and stored in CologneDAO vault list
     */
    @Test
    fun createVault() {
        assertEquals(0, cologneDAO.listVaults().send().size)

        val vault = helper.createVault(helper.credentialsAlice, BigInteger.ZERO, BigInteger.ZERO)

        val vaults = cologneDAO.listVaults().send()
        assertEquals(1, vaults.size)
        assertEquals(vault, vaults.last())
    }
}
