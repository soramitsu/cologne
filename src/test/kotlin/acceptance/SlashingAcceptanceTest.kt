package acceptance

import helpers.VaultState
import org.junit.jupiter.api.Test
import org.testcontainers.junit.jupiter.Testcontainers
import java.math.BigInteger
import org.junit.jupiter.api.Assertions.assertEquals

@Testcontainers
class SlashingAcceptanceTest : AcceptanceTest() {

    /**
     * @given a co-skakeholder has 12 CLGN and the vault is deployed
     * @when the vault is closed
     * @then the vault owner gets CLGN staked on close AND
     * the co-skakeholder can withdraw stake
     */
    @Test
    fun coStakeCloseAndWithdraw() {
        val ownerStakeAmount = toTokenAmount(123)
        ownerCreatesVault()
        ownerStake(ownerStakeAmount)
        val coStakeholder = buyer
        val vaultByCoStakeholder = vaultByBuyer

        val coStakeAmount = toTokenAmount(234)
        stake(coStakeholder, coStakeAmount)

        assertEquals(BigInteger.ZERO, clgnToken.balanceOf(owner.address).send())
        assertEquals(ownerStakeAmount, vaultByOwner.getStake(owner.address).send())
        vaultByOwner.close().send()
        assertEquals(ownerStakeAmount, clgnToken.balanceOf(owner.address).send())
        assertEquals(BigInteger.ZERO, clgnToken.balanceOf(coStakeholder.address).send())

        assertEquals(coStakeAmount, vaultByOwner.getStake(coStakeholder.address).send())
        vaultByCoStakeholder.withdrawStake().send()
        assertEquals(BigInteger.ZERO, vaultByCoStakeholder.getStake(coStakeholder.address).send())
        assertEquals(coStakeAmount, clgnToken.balanceOf(coStakeholder.address).send())
    }

    /**
     * @given vault owner staked 200 CLGN and co-stakeholder staked 100 CLGN and vault is defaulted
     * and initial liquidity auction failed and debt is 50% of staked amount (150 CLGN)
     * @when the vault is slashed
     * @then
     */
    @Test
    fun coStakeSlashed() {
        val ownerStakeAmount = toTokenAmount(200)
        ownerCreatesVault(toTokenAmount(1200), toTokenAmount(1))
        ownerStake(ownerStakeAmount)
        val coStakeholder = buyer
        val coStakeAmount = toTokenAmount(100)
        stake(coStakeholder, coStakeAmount)

        // withdraws 300 EAU
        ownerBreachesVault()
        assertEquals(toTokenAmount(300), vaultByOwner.totalDebt.send())
        startInitialAuction()
        failInitialAuction()

        assertEquals(toTokenAmount(600), vaultByOwner.collateralInEau.send())

        // slash
        vaultBySlasher.slash().send()

        // current stake = initial stake (600) - debt (300) - panalty (10% = 30) = 270
        // owner stake = 100 / 2 - 10% = 90
        // co-stakeholder stake = 100 / 2 -10% = 45
        val expectedOwnerStake = toTokenAmount(90)
        val expectedCoStakeholderStake = toTokenAmount(45)

        assertEquals(toTokenAmount(270), vaultByOwner.collateralInEau.send())
        assertEquals(expectedOwnerStake, vaultByOwner.getStake(owner.address).send())
        assertEquals(expectedCoStakeholderStake, vaultByOwner.getStake(coStakeholder.address).send())

        val vaultByCoStakeholder = vaultByBuyer
        vaultByOwner.close().send()
        assertEquals(expectedOwnerStake, clgnToken.balanceOf(owner.address).send())
        assertEquals(BigInteger.ZERO, clgnToken.balanceOf(coStakeholder.address).send())

        assertEquals(expectedCoStakeholderStake, vaultByOwner.getStake(coStakeholder.address).send())
        vaultByCoStakeholder.withdrawStake().send()
        assertEquals(BigInteger.ZERO, vaultByCoStakeholder.getStake(coStakeholder.address).send())
        assertEquals(expectedCoStakeholderStake, clgnToken.balanceOf(coStakeholder.address).send())
    }
}
