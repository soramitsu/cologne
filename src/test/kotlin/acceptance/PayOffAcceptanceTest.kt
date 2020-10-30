package acceptance

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.testcontainers.junit.jupiter.Testcontainers
import java.math.BigInteger

@Testcontainers
class PayOffAcceptanceTest : AcceptanceTest() {

    /**
     * Check total fees repaid is increased after fees are repaid and fee rate is discounted
     * @given a vault with fees accrued more 1_000_000 EAU
     * @when the owner pays off fees
     * @then the total fees repaid is increased
     */
    @Test
    fun payOffFeeDependsOnHistory() {
        ownerCreatesVault(initialAmount = toTokenAmount(10_000), tokenPrice = toTokenAmount(40))

        val eauToBorrow = toTokenAmount(10_000)
        vaultByOwner.borrow(eauToBorrow).send()
        // pass one year to accrue fees (more 1_000_000 EAU)
        passTime(BigInteger.valueOf(365 * 24 * 3600))
        assertTrue(toTokenAmount(1_000) <= vaultByOwner.fees.send())
        assertEquals(BigInteger.ZERO, vaultByOwner.totalFeesRepaid.send())

        // interest rate is not discounted (101%)
        assertEquals(toInterestRate(101, 100), vaultByOwner.feeRate.send())

        // pay off 1_000, interest rate is 100,9%
        var feeToPayOff = toTokenAmount(1)
        ownerPaysOff(feeToPayOff)
        assertEquals(toTokenAmount(1), vaultByOwner.totalFeesRepaid.send())
        assertEquals(toInterestRate(1009999, 1000000), vaultByOwner.feeRate.send())

        // pay off 1_000 more, interest rate is 100,8%
        ownerPaysOff(feeToPayOff)
        assertEquals(toTokenAmount(2), vaultByOwner.totalFeesRepaid.send())
        assertEquals(toInterestRate(1009998, 1000000), vaultByOwner.feeRate.send())

        // pay off 997_000 more, total 999_000, interest rate is 1,1%
        feeToPayOff = toTokenAmount(9_998)
        ownerPaysOff(feeToPayOff)
        assertEquals(toTokenAmount(10_000), vaultByOwner.totalFeesRepaid.send())
        assertEquals(toInterestRate(1000000, 1000000), vaultByOwner.feeRate.send())
    }

    /**
     * Check total fees rate is discounted after
     * @given a vault with stake
     * @when the owner stake
     * @then the fees rate is discounted
     */
    @Test
    fun payOffFeeDependsOnStake() {
        ownerCreatesVault(initialAmount = toTokenAmount(800), tokenPrice = toTokenAmount(1))

        // No debt: rate == 1%
        assertEquals(toInterestRate(1, 100), vaultByOwner.feeRate.send())

        vaultByOwner.borrow(toTokenAmount(200)).send()

        // collateral / debt = 0%: rate 101%
        assertEquals(toInterestRate(101, 100), vaultByOwner.feeRate.send())

        // collateral / debt = 25%: rate 76%
        ownerStake(toTokenAmount(25))
        assertEquals(toInterestRate(76, 100), vaultByOwner.feeRate.send())

        // collateral / debt = 50%: rate 51%
        ownerStake(toTokenAmount(25))
        assertEquals(toInterestRate(51, 100), vaultByOwner.feeRate.send())

        // collateral / debt = 99%: rate 2%
        ownerStake(toTokenAmount(49))
        assertEquals(toInterestRate(2, 100), vaultByOwner.feeRate.send())

        // collateral / debt = 100%: rate 1%
        ownerStake(toTokenAmount(1))
        assertEquals(toInterestRate(1, 100), vaultByOwner.feeRate.send())

        // collateral / debt > 100%: rate 1%
        ownerStake(toTokenAmount(10))
        assertEquals(toInterestRate(1, 100), vaultByOwner.feeRate.send())
    }
}
