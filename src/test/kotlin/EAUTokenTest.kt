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

/**
 * Test EAU token
 */
@Testcontainers
class EAUTokenTest {

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
        alice = helper.credentialsAlice.address
        bob = helper.credentialsBob.address
        cashStash = helper.eauToken.contractAddress
    }

    /**
     * @given EAU Token and Alice has 20 EAU and Bob has 10 tokens and contract distribution stash is 0 EAU
     * @when Alice distributes 10 EAU tokens
     * @then Alice has 10 EAU and EAU contract has 10 EAU and dividends accrued to Alice are 5 EAU and dividends accrued
     * to Bob are 5 EAU
     */
    @Test
    fun testEAUDistribution() {
        helper.addEAU(alice, BigInteger.valueOf(20))
        helper.addEAU(bob, BigInteger.valueOf(10))
        assertEquals(BigInteger.ZERO, helper.eauToken.balanceOf(cashStash).send())

        helper.distributeEAU(alice, BigInteger.valueOf(10))

        assertEquals(BigInteger.TEN, helper.eauToken.balanceOf(alice).send())
        assertEquals(BigInteger.TEN, helper.eauToken.balanceOf(bob).send())
        assertEquals(BigInteger.valueOf(5), helper.eauToken.dividensAccrued(alice).send())
        assertEquals(BigInteger.valueOf(5), helper.eauToken.dividensAccrued(bob).send())
        assertEquals(BigInteger.TEN, helper.eauToken.balanceOf(cashStash).send())
    }

    /**
     * @given EAU Token has 10 EAU and Bob has 10 EAU and Bob has dividends accrued of 5 EAU
     * @when Bob withdraws dividend
     * @then Bob has 15 EAU and dividends not reclaimed are 5 EAU and dividends accrued to Bob are 0 EAU
     */
    @Test
    fun testEAUWithdrawal() {
        helper.addEAU(alice, BigInteger.valueOf(20))
        helper.addEAU(bob, BigInteger.valueOf(10))
        assertEquals(BigInteger.ZERO, helper.eauToken.balanceOf(cashStash).send())
        helper.distributeEAU(alice, BigInteger.valueOf(10))

        helper.eauToken.withdrawDividends(bob).send()

        assertEquals(BigInteger.valueOf(15), helper.eauToken.balanceOf(bob).send())
        assertEquals(BigInteger.ZERO, helper.eauToken.dividensAccrued(bob).send())
        assertEquals(BigInteger.valueOf(5), helper.eauToken.balanceOf(cashStash).send())
    }

    /**
     * @given Alice has 2 EAU and Bob has 1 EAU
     * @when Alice distributes 1 EAU
     * @then Alice dividens accrued is 0 EAU and Bob dividens accrued is 0 EAU due to rounding and dividends
     * not withdrawn is 1 EAU
     */
    @Test
    fun distributionLeftover() {
        helper.addEAU(alice, BigInteger.TWO)
        helper.addEAU(bob, BigInteger.ONE)
        assertEquals(BigInteger.ZERO, helper.eauToken.balanceOf(cashStash).send())

        helper.distributeEAU(alice, BigInteger.ONE)

        assertEquals(BigInteger.ZERO, helper.eauToken.dividensAccrued(alice).send())
        assertEquals(BigInteger.ZERO, helper.eauToken.dividensAccrued(bob).send())
        assertEquals(BigInteger.ONE, helper.eauToken.balanceOf(cashStash).send())
    }
}
