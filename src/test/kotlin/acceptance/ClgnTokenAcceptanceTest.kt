package acceptance

import contract.CLGNToken
import helpers.ContractTestHelper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.GenericContainer
import org.testcontainers.images.builder.ImageFromDockerfile
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.web3j.crypto.Credentials
import org.web3j.protocol.exceptions.TransactionException
import java.math.BigInteger

@Testcontainers
class ClgnTokenAcceptanceTest {
    @Container
    private val ganache: GenericContainer<Nothing> =
        GenericContainer<Nothing>(
            ImageFromDockerfile()
                .withDockerfile(Path.of(javaClass.getResource("/docker/ganache/Dockerfile").toURI()))
        )
            .withExposedPorts(8545)

    lateinit var helper: ContractTestHelper
    lateinit var clgnToken: CLGNToken
    lateinit var creator: Credentials
    lateinit var medleyDaoAddress: String

    @BeforeEach
    fun setUp() {
        helper = ContractTestHelper(ganache.host, ganache.firstMappedPort)
        clgnToken = helper.clgnToken
        creator = helper.credentialsSeed
        medleyDaoAddress = helper.medleyDAO.contractAddress
    }

    /**
     * @given CLGN token
     * @when CLGN token is deployed
     * @then has:
     * - intial supply of 22,5 kk on owner
     * - precision is 18
     * - owner is MedleyDAO
     */
    @Test
    fun clgnTokenCreation() {
        val decimals = BigInteger.TEN.pow(18)
        val initialSupply = BigInteger.valueOf(22500000).multiply(decimals);

        clgnToken = CLGNToken.deploy(helper.web3, creator, helper.gasProvider).send()

        assertEquals(initialSupply, clgnToken.totalSupply().send());
        assertEquals(initialSupply, clgnToken.balanceOf(creator.address).send())
        assertEquals(BigInteger.valueOf(18), clgnToken.decimals().send())
        assertEquals(creator.address, clgnToken.owner().send())
    }

    /**
     * @given a CLGN token deployed
     * @when mint called by not owner
     * @then error returned - only owner can mint
     */
    @Test
    fun onlyOwnerCanMint() {
        val stranger = helper.credentialsAlice
        val clgnByStranger = CLGNToken.load(clgnToken.contractAddress, helper.web3, stranger, helper.gasProvider)

        assertThrows<TransactionException> {
            clgnByStranger.mint(clgnByStranger.contractAddress, BigInteger.ONE).send()
        }
    }
}
