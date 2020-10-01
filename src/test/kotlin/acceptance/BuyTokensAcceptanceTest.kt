package acceptance

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.testcontainers.junit.jupiter.Testcontainers
import org.web3j.protocol.exceptions.TransactionException
import java.math.BigInteger
import org.junit.jupiter.api.Assertions.assertEquals

@Testcontainers
class BuyTokensAcceptanceTest : AcceptanceTest() {

    val toBuy = toTokenAmount(20)

    /**
     * @given a vault has at least 20 TKN (user tokens) and a buyer has 40 EAU and price is 2 TKN/EAU
     * @when the buyer buys 20 TKN at price at least 2 TKN/EAU
     * @then 40 EAU go to the vault and 20 TKN go to the buyer's account
     */
    @Test
    fun buyTokens() {
        ownerCreatesVault()
        val costInEau = getEauToBuyUserTokenAmount(toBuy)
        helper.addEAU(buyer.address, costInEau)
        assertEquals(initialAmount, vaultByBuyer.tokenAmount.send())

        eauTokenByBuyer.approve(vaultByBuyer.contractAddress, costInEau).send()
        vaultByBuyer.buy(toBuy, tokenPrice, buyer.address).send()

        assertEquals(BigInteger.ZERO, eauTokenByBuyer.allowance(buyer.address, vaultByBuyer.contractAddress).send())
        assertEquals(costInEau, eauTokenByBuyer.balanceOf(vaultByBuyer.contractAddress).send())
        assertEquals(BigInteger.ZERO, eauTokenByBuyer.balanceOf(buyer.address).send())
        assertEquals(initialAmount.minus(toBuy), userToken.balanceOf(vaultByBuyer.contractAddress).send())
        assertEquals(toBuy, userToken.balanceOf(buyer.address).send())
        assertEquals(initialAmount - toBuy, vaultByBuyer.tokenAmount.send())
    }

    /**
     * @given a vault has insufficient user token (1 TKN) and a buyer has 40 EAU and price is 2 TKN/EAU
     * @when the buyer buys 20 TKN with price at least 2 TKN/EAU
     * @then deal rejected, balances are not changed
     */
    @Test
    fun buyTokensNotEnoughUserTokens() {
        val insufficientAmount = BigInteger.ONE
        ownerCreatesVault(initialAmount = insufficientAmount)
        val costInEau = getEauToBuyUserTokenAmount(toBuy)
        helper.addEAU(buyer.address, costInEau)

        eauTokenByBuyer.approve(vaultByBuyer.contractAddress, costInEau).send()
        assertThrows<TransactionException> {
            vaultByBuyer.buy(toBuy, tokenPrice, buyer.address).send()
        }

        assertEquals(BigInteger.ZERO, eauTokenByBuyer.balanceOf(vaultByBuyer.contractAddress).send())
        assertEquals(costInEau, eauTokenByBuyer.balanceOf(buyer.address).send())
        assertEquals(insufficientAmount, userToken.balanceOf(vaultByBuyer.contractAddress).send())
        assertEquals(BigInteger.ZERO, userToken.balanceOf(buyer.address).send())
    }

    /**
     * @given a vault has enough user token and a buyer with 40 EAU and price is 2 TKN/EAU
     * @when the buyer buys 20 TKN with price at least 1 TKN/EAU
     * @then deal rejected, balances are not changed
     */
    @Test
    fun buyTokensPriceTooLow() {
        ownerCreatesVault()
        val priceTooHigh = BigInteger.ONE
        val costInEau = getEauToBuyUserTokenAmount(toBuy, priceTooHigh)
        helper.addEAU(buyer.address, costInEau)

        eauTokenByBuyer.approve(vaultByBuyer.contractAddress, costInEau).send()
        assertThrows<TransactionException> {
            vaultByBuyer.buy(toBuy, priceTooHigh, buyer.address).send()
        }

        assertEquals(BigInteger.ZERO, eauTokenByBuyer.balanceOf(vaultByBuyer.contractAddress).send())
        assertEquals(costInEau, eauTokenByBuyer.balanceOf(buyer.address).send())
        assertEquals(initialAmount, userToken.balanceOf(vaultByBuyer.contractAddress).send())
        assertEquals(BigInteger.ZERO, userToken.balanceOf(buyer.address).send())
    }
}
