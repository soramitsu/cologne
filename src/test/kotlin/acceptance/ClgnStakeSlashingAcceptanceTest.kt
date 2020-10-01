package acceptance

import helpers.VaultState
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.testcontainers.junit.jupiter.Testcontainers
import org.web3j.protocol.exceptions.TransactionException
import java.math.BigInteger

@Testcontainers
class ClgnStakeSlashingAcceptanceTest : AcceptanceTest() {

    /**
     * Fail initial liquidity auction
     */
    fun failInitialAuction() {
        // Dutch auction has passed
        val time = helper.timeProvider.time.send().add(BigInteger.valueOf(180000))
        helper.timeProvider.setTime(time).send()
    }

    /**
     * @given the vault is closed
     * @when call slash
     * @then error returned - closed
     */
    @Test
    fun slashClosed() {
        ownerCreatesVault()
        vaultByOwner.close().send()

        assertThrows<TransactionException> {
            vaultBySlasher.slash().send()
        }
    }

    /**
     * @given the vault is open and has no debt
     * @when call slash
     * @then error returned - no debt
     */
    @Test
    fun slashNoBreach() {
        ownerCreatesVault()

        assertThrows<TransactionException> {
            vaultBySlasher.slash().send()
        }
    }

    /**
     * @given the vault is open and initial liquidity auction is in process
     * @when call slash
     * @then error returned - no debt
     */
    @Test
    fun slashAuctionNotOver() {
        ownerCreatesVault()
        ownerBreachesVault()

        assertThrows<TransactionException> {
            vaultBySlasher.slash().send()
        }
    }

    /**
     * @given the vault is breached and initial liquidity auction is over and stake is 1000 CLGN
     * @when stake is slashed
     * @then penalty is distributed:
     *  2,5% of stake (25 CLGN) goes to Initial Liquidity Auction initiator
     *  2,5% of stake (25 CLGN) goes to slashing initiator
     *  5% of stake (50 CLGN) is burned
     */
    @Test
    fun slashBountyDistribution() {
        helper.addEAU(helper.marketAdaptor.contractAddress, toTokenAmount(2000))
        ownerCreatesVault(initialAmount = toTokenAmount(16000))
        ownerStake(toTokenAmount(1000))
        val initialClgnSupply = clgnToken.totalSupply().send()
        ownerBreachesVault()
        vaultByInitiator.startInitialLiquidityAuction().send()
        failInitialAuction()
        assertEquals(VaultState.WaitingForSlashing.toBigInteger(), vaultByOwner.state.send())

        vaultBySlasher.slash().send()

        assertEquals(toTokenAmount(25), clgnToken.balanceOf(initiator.address).send())
        assertEquals(toTokenAmount(25), clgnToken.balanceOf(slasher.address).send())
        assertEquals(initialClgnSupply.subtract(toTokenAmount(50)), clgnToken.totalSupply().send())
    }

    /**
     * @given the vault has principal debt of 4000 EAU and some fee accrued and stake of 1000 CLGN (assessed as 2000
     * EAU) and initial liquidity auction failed
     * @when slashing called
     * @then principal is paid off partially (1800 EAU paid off and 2200 is current debt) and fees are forgiven and
     * penalty is distributed and stake is slashed
     */
    @Test
    fun slashPrincipalPartiallyCovered() {
        helper.addEAU(helper.marketAdaptor.contractAddress, toTokenAmount(2000))
        ownerCreatesVault(initialAmount = toTokenAmount(16000), tokenPrice = toTokenAmount(1))
        ownerStake(toTokenAmount(1000))
        ownerBreachesVault()
        // wait 5 days for fees and ensure fees accrued
        helper.passTime(BigInteger.valueOf(5 * 24 * 3600))
        assertNotEquals(BigInteger.ZERO, vaultByOwner.fees.send())
        // start and fail Initial Liquidity Auction
        vaultByInitiator.startInitialLiquidityAuction().send()
        failInitialAuction()

        vaultBySlasher.slash().send()

        assertEquals(toTokenAmount(2200), vaultByOwner.totalDebt.send())
        assertEquals(toTokenAmount(2200), vaultByOwner.principal.send())
        assertEquals(BigInteger.ZERO, vaultByOwner.fees.send())
        assertEquals(BigInteger.ZERO, vaultByOwner.collateralInEau.send())
    }

    /**
     * @given the vault has principal debt of 10000 EAU and fees accrued more then 2 EAU and stake of 5600 CLGN
     * (assessed as 11200 EAU) and initial liquidity auction failed
     * @when slashing called
     * @then principal is paid off (10000 EAU) and penalty paid off (10000 EAU) and fees paid off partially (100 EAU) and
     * the rest of fees are forgiven
     */
    @Test
    fun slashPrincipalsCoveredAndFeesPatiallyCovered() {
        helper.addEAU(helper.marketAdaptor.contractAddress, toTokenAmount(20000))
        ownerCreatesVault(initialAmount = toTokenAmount(40000), tokenPrice = toTokenAmount(1))
        ownerStake(toTokenAmount(5550))
        ownerBreachesVault()
        // ensure fees are not covered by stake
        helper.passTime(BigInteger.valueOf(100 * 24 * 3600))
        assertTrue(vaultByOwner.fees.send() > BigInteger.TWO)
        // start and fail Initial Liquidity Auction
        vaultByInitiator.startInitialLiquidityAuction().send()
        failInitialAuction()

        vaultBySlasher.slash().send()

        assertEquals(BigInteger.ZERO, vaultByOwner.fees.send())
        assertEquals(BigInteger.ZERO, vaultByOwner.totalDebt.send())
        assertEquals(BigInteger.ZERO, vaultByOwner.collateralInEau.send())
    }

    /**
     * @given the vault has principal debt of 10000 EAU and fees accrued more than 1000 EAU and stake of 10000 CLGN
     * (assessed as 20000 EAU) and initial liquidity auction failed
     * @when slashing called
     * @then principal is paid off (10000 EAU) and penalty paid off (1010 EAU) and fees paid off (100 EAU) and stake
     * leftover in EAU is 4445 CLGN (8890 EAU)
     */
    @Test
    fun slashCovered() {
        helper.addEAU(helper.marketAdaptor.contractAddress, toTokenAmount(20000))
        ownerCreatesVault(initialAmount = toTokenAmount(40000), tokenPrice = toTokenAmount(1))
        ownerStake(toTokenAmount(10_000))
        ownerBreachesVault()
        // ensure fees are not covered by stake
        helper.passTime(BigInteger.valueOf(40 * 24 * 3600))
        assertEquals(BigInteger("109783341512316737457"), vaultByOwner.fees.send())
        // start and fail Initial Liquidity Auction
        vaultByInitiator.startInitialLiquidityAuction().send()
        failInitialAuction()

        vaultBySlasher.slash().send()

        assertEquals(BigInteger.ZERO, vaultByOwner.fees.send())
        assertEquals(BigInteger.ZERO, vaultByOwner.totalDebt.send())
        assertEquals(BigInteger("8879238324336451588800"), vaultByOwner.collateralInEau.send())
    }
}
