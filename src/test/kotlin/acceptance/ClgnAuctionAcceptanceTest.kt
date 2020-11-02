package acceptance

import contract.Vault
import helpers.VaultState
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.testcontainers.junit.jupiter.Testcontainers
import org.web3j.protocol.exceptions.TransactionException
import java.math.BigInteger
import org.junit.jupiter.api.Assertions.assertEquals

@Testcontainers
class ClgnAuctionAccept : AcceptanceTest() {

    /**
     * @given closed vault
     * @when cover shortfall called
     * @then error returned - cannot call closed vault
     */
    @Test
    fun coverShortfallClose() {
        ownerCreatesVault()
        val coverInitiatorVault =
            Vault.load(vaultByOwner.contractAddress, helper.web3, helper.credentialsDave, helper.gasProvider)
        vaultByOwner.close().send()

        assertThrows<TransactionException> {
            coverInitiatorVault.coverShortfall().send()
        }
    }

    /**
     * @given the vault is breached and auction has not been over yet
     * @when cover shortfall called
     * @then error returned - cannot call closed vault
     */
    @Test
    fun coverShortfallAuction() {
        ownerCreatesVault()
        ownerStake100Percent()
        val coverInitiatorVault =
            Vault.load(vaultByOwner.contractAddress, helper.web3, helper.credentialsDave, helper.gasProvider)
        ownerBreachesVault()
        vaultByInitiator.startInitialLiquidityAuction().send()

        assertThrows<TransactionException> {
            coverInitiatorVault.coverShortfall().send()
        }
    }

    /**
     * @given the vault is breached and the initial liquidity auction is over and not slashed
     * @when cover shortfall called
     * @then error returned - should be slashed
     */
    @Test
    fun coverShortfallNotSlashed() {
        ownerCreatesVault()
        ownerStake100Percent()
        val coverInitiatorVault =
            Vault.load(vaultByOwner.contractAddress, helper.web3, helper.credentialsDave, helper.gasProvider)
        ownerBreachesVault()
        vaultByInitiator.startInitialLiquidityAuction().send()
        failInitialAuction()

        assertThrows<TransactionException> {
            coverInitiatorVault.coverShortfall().send()
        }
    }

    /**
     * @given the vault is breached and the initial liquidity auction is over and caller doesn't have CLGN
     * @when cover shortfall called
     * @then error returned - CLGN holder with at least 5% of total remaining outstanding EAU notional in the defaulted
     * Vault can initiate a CLGN mint
     */
    @Test
    fun coverShortfallWrongInitiator() {
        ownerCreatesVault()
        ownerStake100Percent()
        val coverInitiatorVault =
            Vault.load(vaultByOwner.contractAddress, helper.web3, helper.credentialsDave, helper.gasProvider)
        ownerBreachesVault()
        vaultByInitiator.startInitialLiquidityAuction().send()
        failInitialAuction()

        assertThrows<TransactionException> {
            coverInitiatorVault.coverShortfall().send()
        }
    }

    /**
     * @given the vault is slashed and has debt of 1000 EAU and cover initiator has 25 CLGN (assessed as 50 EAU) which
     * is 5% of outstanding debt
     * @when cover shortfall called
     * @then 550 CLGN minted and sold for 1100 EAU, 1000 EAU are paid off and 100 EAU paid to initiator as bounty
     */
    @Test
    fun coverShortfall() {
        val coverInitiator = helper.credentialsDave
        helper.addEAU(helper.marketAdaptor.contractAddress, toTokenAmount(1100))
        helper.addCLGN(coverInitiator.address, toTokenAmount(25))
        ownerCreatesVault()
        val toStake = toTokenAmount(400);
        ownerStake(toStake)
        val coverInitiatorVault =
            Vault.load(vaultByOwner.contractAddress, helper.web3, coverInitiator, helper.gasProvider)
        ownerBreachesVault()
        vaultByInitiator.startInitialLiquidityAuction().send()
        failInitialAuction()
        assertEquals(BigInteger.ZERO, vaultByOwner.fees.send())
        assertEquals(toTokenAmount(1000), vaultByOwner.principal.send())
        val initialClgnSupply = clgnToken.totalSupply().send()
        assertEquals(VaultState.WaitingForSlashing.toBigInteger(), vaultByOwner.state.send())
        vaultBySlasher.slash().send()
        assertEquals(VaultState.WaitingForClgnAuction.toBigInteger(), vaultByOwner.state.send())

        coverInitiatorVault.coverShortfall().send()

        assertEquals(VaultState.Slashed.toBigInteger(), vaultByOwner.state.send())
        assertEquals(toTokenAmount(134), clgnToken.totalSupply().send().subtract(initialClgnSupply))
        assertEquals(BigInteger.ZERO, vaultByOwner.getTotalDebt().send())
        assertEquals(toTokenAmount(28), eauToken.balanceOf(coverInitiator.address).send())
        assertEquals(VaultState.Slashed.toBigInteger(), vaultByOwner.state.send())
    }
}
