import acceptance.AcceptanceTest
import contract.*
import helpers.VaultState
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.testcontainers.junit.jupiter.Testcontainers
import org.web3j.protocol.exceptions.TransactionException
import java.math.BigInteger

@Testcontainers
class VaultTest : AcceptanceTest() {

    /**
     * @given MedleyDAO deployed and no vaults created and user has 100 UserTokens
     * @when create vault called with 100 UserTokens by owner and declare price as 2 EAU/Token
     * @then new vault created and vault owner is caller and 100 UserTokens transferred to the vault and credit limit is
     * 25% of assessed value (100 TKN * 2 EAU / 4 = 50 EAU)
     */
    @Test
    fun createVault() {
        val initialAmount = toTokenAmount(100)
        val tokenPrice = toTokenAmount(2)
        ownerCreatesVault(initialAmount = initialAmount, tokenPrice = tokenPrice)

        assertEquals(VaultState.Trading.toBigInteger(), vaultByOwner.state.send())
        assertEquals(owner.address, vaultByOwner.owner().send())
        assertEquals(initialAmount, userToken.balanceOf(vaultByOwner.contractAddress).send())
        assertEquals(BigInteger.ZERO, userToken.balanceOf(owner.address).send())
        assertEquals(tokenPrice, vaultByOwner.price.send())
        assertEquals(toTokenAmount(50), vaultByOwner.canBorrow().send())
        assertEquals(initialAmount, vaultByOwner.tokenAmount.send())
    }

    /**
     * @given Vault deployed and has user tokens, CLGN and EAU and has no debt
     * @when the owner closes vault
     * @then all assets (user tokens left in vault, EAU and CLGN staked by the owner transferred
     * from the vault to the owner, vault is closed
     */
    @Test
    fun closeNoDebt() {
        val stake = toTokenAmount(20)
        ownerCreatesVault(initialAmount, tokenPrice)
        val eauBalance = toTokenAmount(123)
        helper.addEAU(vaultByOwner.contractAddress, eauBalance)
        ownerStake(stake)

        vaultByOwner.close().send()

        assertEquals(VaultState.Closed.toBigInteger(), vaultByOwner.state.send())
        assertEquals(initialAmount, userToken.balanceOf(owner.address).send())
        assertEquals(eauBalance, eauToken.balanceOf(owner.address).send())
        assertEquals(stake, clgnToken.balanceOf(owner.address).send())
        assertEquals(BigInteger.ZERO, vaultByOwner.totalDebt.send())
    }

    /**
     * @given a vault owner has debt
     * @when close is called
     * @then error response - not possilbe to close vault with debt
     */
    @Test
    fun closeWithDebtNotAllowed() {
        ownerCreatesVault(initialAmount, tokenPrice)
        val toBorrow = toTokenAmount(10)
        vaultByOwner.borrow(toBorrow).send()

        assertEquals(VaultState.Trading.toBigInteger(), vaultByOwner.state.send())
        assertThrows<TransactionException> {
            vaultByOwner.close().send()
        }
    }

    /**
     * @given a vault and stranger account
     * @when close is called by stranger
     * @then error response - only owner allowed to close the vault
     */
    @Test
    fun closeByStranger() {
        ownerCreatesVault(initialAmount, tokenPrice)
        val stranger = helper.credentialsBob
        val strangerVault = Vault.load(vaultByOwner.contractAddress, helper.web3, stranger, helper.gasProvider)

        assertThrows<TransactionException> {
            strangerVault.close().send()
        }
    }

    /**
     * @given MedleyDAO deployed owner can borrow 50 EAU
     * @when the owner borrows 50 EAU
     * @then EAU tokens are minted to the owner account, owner debt is 50 EAU
     */
    @Test
    fun borrowSunnyDay() {
        ownerCreatesVault(initialAmount, tokenPrice)
        val initialEauSupply = helper.eauToken.totalSupply().send()
        val initialOwnerEauBalance = helper.eauToken.balanceOf(owner.address).send()
        val toBorrow = vaultByOwner.canBorrow().send()

        vaultByOwner.borrow(toBorrow).send()

        assertEquals(initialEauSupply.plus(toBorrow), helper.eauToken.totalSupply().send())
        assertEquals(initialOwnerEauBalance.plus(toBorrow), helper.eauToken.balanceOf(owner.address).send())
        assertEquals(toBorrow, vaultByOwner.principal.send())
        assertEquals(toBorrow, vaultByOwner.getTotalDebt().send())
    }

    /**
     * @given a vault is deployed by the owner
     * @when a stranger account sends borrow
     * @then error returned - operation is restricted to the owner only
     */
    @Test
    fun borrowBy3rdParty() {
        ownerCreatesVault(initialAmount, tokenPrice)
        val stranger = helper.credentialsBob
        val strangerVault = Vault.load(vaultByOwner.contractAddress, helper.web3, stranger, helper.gasProvider)
        val toBorrow = toTokenAmount(10)

        assertThrows<TransactionException> {
            strangerVault.borrow(toBorrow).send()
        }
    }

    /**
     * @given MedleyDAO deployed owner has borrowed all limit
     * @when the owner borrows 50 EAU more
     * @then Error returned - credit limit exhausted
     */
    @Test
    fun borrowExceedsLimit() {
        ownerCreatesVault(initialAmount, tokenPrice)
        val toBorrow = vaultByOwner.canBorrow().send()
        vaultByOwner.borrow(toBorrow).send()

        assertThrows<TransactionException> {
            vaultByOwner.borrow(toBorrow).send()
        }
    }

    /**
     * @given The vault deployed with no debt
     * @when the user pays off 5000 EAU
     * @then the vault balance increased by 5000 EAU
     */
    @Test
    fun payOffNoDebt() {
        ownerCreatesVault()
        val toPayOff = toTokenAmount(5_000)
        helper.addEAU(owner.address, toPayOff)

        ownerPaysOff(toPayOff)

        assertEquals(toPayOff, eauToken.balanceOf(vaultByOwner.contractAddress).send())
        assertEquals(BigInteger.ZERO, eauToken.balanceOf(owner.address).send())
    }

    /**
     * @given Vault is created and owner debt is 10'000 EAU and 0 fees accrued
     * @when the owner pays off 5'000 EAU
     * @then Debt is reduced to 5'000 EAU and 5'000 EAU are burnt
     */
    @Test
    fun payOffDebtPartially() {
        val initialAmount = toTokenAmount(50_000)
        val tokenPrice = toTokenAmount(4)
        ownerCreatesVault(initialAmount, tokenPrice)
        val debtBefore = toTokenAmount(10_000)
        vaultByOwner.borrow(debtBefore).send()
        val toPayOff = toTokenAmount(5_000)
        val initialEauSupply = eauToken.totalSupply().send()
        val balanceBefore = eauToken.balanceOf(owner.address).send()

        ownerPaysOff(toPayOff)

        val newDebt = vaultByOwner.getTotalDebt().send()
        assertEquals(debtBefore.minus(toPayOff), newDebt)
        assertEquals(initialEauSupply.minus(toPayOff), eauToken.totalSupply().send())
        assertEquals(balanceBefore.minus(toPayOff), eauToken.balanceOf(owner.address).send())
    }
}
