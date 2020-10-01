package acceptance

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.testcontainers.junit.jupiter.Testcontainers
import java.math.BigInteger

@Testcontainers
class PayOffAcceptanceTest: AcceptanceTest() {

    /**
     * Check total fees repaid is increased after fees are repaid
     * @given a vault with fees accrued
     * @when the owner pays off fees
     * @then the total fees repaid is increased
     */
    @Test
    fun payOffFee() {
        ownerCreatesVault()

        val toBorrow = toTokenAmount(1_000)
        vaultByOwner.borrow(toBorrow).send()
        // pass one year to accrue fees
        passTime(BigInteger.valueOf(365*24*3600))
        val feeToPayOff = toTokenAmount(100)
        assertTrue(feeToPayOff <= vaultByOwner.fees.send())
        assertEquals(BigInteger.ZERO, vaultByOwner.totalFeesRepaid.send())

        ownerPaysOff(feeToPayOff)

        assertEquals(feeToPayOff, vaultByOwner.totalFeesRepaid.send())
    }
}
