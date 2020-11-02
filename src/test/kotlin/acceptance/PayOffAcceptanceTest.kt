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
        val tokenPrice = toTokenAmount(40)
        ownerCreatesVault(initialAmount = toTokenAmount(10_000), tokenPrice = tokenPrice)
        ownerStake(toTokenAmount(4_000))

        val eauToBorrow = toTokenAmount(10_000)
        vaultByOwner.borrow(eauToBorrow).send()
        // pass some years to accrue fees (more 1_000 EAU)
        while (toTokenAmount(200_000) >= vaultByOwner.fees.send()) {
            passTime(BigInteger.valueOf(100 * 24 * 3600))
            vaultByOwner.updateDebt().send()
        }
        assertEquals(BigInteger.ZERO, vaultByOwner.totalFeesRepaid.send())

        // interest rate is not discounted (21%)
        assertEquals(toInterestRate(21, 100), vaultByOwner.feeRate.send())

        // pay off 1, interest rate is 20,9999%
        var feeToPayOff = toTokenAmount(1)
        ownerPaysOff(feeToPayOff)
        assertEquals(toTokenAmount(1), vaultByOwner.totalFeesRepaid.send())
        assertEquals(toInterestRate(209999, 1000000), vaultByOwner.feeRate.send())

        // pay off 1 more, interest rate is 20,9998%
        ownerPaysOff(feeToPayOff)
        assertEquals(toTokenAmount(2), vaultByOwner.totalFeesRepaid.send())
        assertEquals(toInterestRate(209998, 1000000), vaultByOwner.feeRate.send())

        // pay off 9_998 more, total 10_000, interest rate is 1%
        feeToPayOff = toTokenAmount(199_998)
        helper.addEAU(owner.address, feeToPayOff)
        ownerPaysOff(feeToPayOff)
        assertEquals(toTokenAmount(200_000), vaultByOwner.totalFeesRepaid.send())
        assertEquals(toInterestRate(1, 100), vaultByOwner.feeRate.send())
    }

    /**
     * Check total fees rate is discounted after staking
     * @given a vault with stake
     * @when the owner stakes
     * @then the fees rate is discounted
     */
    @Test
    fun payOffFeeDependsOnStake() {
        val tokenPrice = toTokenAmount(1)
        ownerCreatesVault(initialAmount = toTokenAmount(800), tokenPrice = tokenPrice)

        // No debt: rate == 1%
        assertEquals(toInterestRate(1, 100), vaultByOwner.feeRate.send())

        // collateral / debt = 80%: rate 21%
        ownerStake(toTokenAmount(40))
        vaultByOwner.borrow(toTokenAmount(100)).send()
        assertEquals(toInterestRate(21, 100), vaultByOwner.feeRate.send())

        // collateral / debt = 99%: rate 2%
        ownerStake(toTokenAmount(95, 10))
        assertEquals(toInterestRate(2, 100), vaultByOwner.feeRate.send())

        // collateral / debt = 100%: rate 1%
        ownerStake(toTokenAmount(1))
        assertEquals(toInterestRate(1, 100), vaultByOwner.feeRate.send())

        // collateral / debt > 100%: rate 1%
        ownerStake(toTokenAmount(10))
        assertEquals(toInterestRate(1, 100), vaultByOwner.feeRate.send())
    }
}
