// SPDX-License-Identifier: MIT

pragma solidity ^0.7.0;

import "./../utils/IOwnable.sol";

interface IVault is IOwnable {

    /**
     * Types of Vault states
     * (Vault created) -> Trading
     * Trading -> (limit is breached) -> Defaulted
     *         -> (close) -> Closed
     * Defaulted -> (paid off) -> Trading
     *           -> (initial liquidity auction started) -> InitialLiquidityAuctionInProcess
     * InitialLiquidityAuctionInProcess -> (sold and debt is covered) -> Trading
     *                                  -> (time elapsed) -> WaitingForSlashing
     * WaitingForSlashing -> (slash cover debt) -> Slashed
     *                    ->  (debt not covered by slashing) -> WaitingForClgnAuction
     * WaitingForClgnAuction -> (cover shortfall auction called) -> Slashed
     * Slashed -> (close) -> Closed
     * Closed - final state
     */
    enum VaultState {
        Trading,
        Defaulted,
        InitialLiquidityAuctionInProcess,
        WaitingForSlashing,
        WaitingForClgnAuction,
        Slashed,
        Closed,
        SoldOut
    }

    /**
     * Returns token address
     */
    function getTokenAddress() external view returns (address);

    /**
     * Stake CLGN on Vault
     * @param amount - amount of attoCLGN to stake
     */
    function stake(uint amount) external;

    /**
     * Get stake of a user in CLGN
     */
    function getStake(address account) external view returns (uint);

    /**
     * Get reward for stake to be paid off
     * The vault owner receives no reward for staking
     * The co-stakeholder receives reward as 50% of fees saved
     */
    function getStakeRewardAccrued(address stakeholder) external view returns (uint);

    /**
     * Get total stake reward to pay off
     */
    function getStakeRewardAccrued() external view returns (uint);

    /**
     * Get stake reward ready to claim
     */
    function getStakeRewardToClaim(address stakeholder) external view returns (uint);

    /**
     * Withdraw reward for stake
     * The vault owner will get nothing
     * The stakeholder will receive reward as 50% of fees saved
     */
    function claimStakeReward(address stakeholder) external returns (uint);

    /**
     * Withdraws stake
     * Can be called only after the vault is closed.
     */
    function withdrawStake() external returns (uint);

    /**
     * @param amount of tokens to buy on atomic smallest part of token (e.g. 10^-18)
     * @param maxPrice of the 1 UserToken in attoEAU
     * @param to - address to send to
     */
    function buy(uint amount, uint maxPrice, address to) external;

    /**
     * Borrows EAU
     * @param amount to borrow in attoEAU
     */
    function borrow(uint amount) external;

    /**
     * Pay off debt
     * @param amount - amount of attoEAU to pay off
     */
    function payOff(uint amount) external;

    function close() external;

    /**
      * Initiate close-out process
      * Can be called by any Ethereum wallet if the vault has breached the liquidity limit.
      * Starts Initial Liquidity Vault Auction.
      */
    function startInitialLiquidityAuction() external;

    function slash() external;

    /**
     * Mint and sell CLGN on CLGN auction to cover shortfall
     */
    function coverShortfall() external;

    /** Maximum amount the owner can borrow (depends on TKN price and amount) */
    function getCreditLimit() external view returns (uint);

    /**
     * Get amount of attoEAU the owner can borrow now before credit limit is exhausted
     */
    function canBorrow() external view returns (uint);

    /**
     * Returns Total debt = principal + fees accrued
     */
    function getTotalDebt() external view returns (uint);

    /**
     * Update debt records
     */
    function updateDebt() external;

    /**
     * Returns debt principal
     */
    function getPrincipal() external view returns (uint);

    /**
     * Returns fees accrued at this moment
     */
    function getFees() external view returns (uint);

    /**
     * Returns total fees repaid by owner (used to get liquidity fee discount)
     */
    function getTotalFeesRepaid() external view returns (uint);

    /**
     * Returns instant annual fee rate (depends on repayment history, collateral, debt and challenge price)
     * Returned value should be multiplied by 10^18 to get interest rate percent.
     */
    function getFeeRate() external view returns (uint);

    /**
     * Returns User Token price in attoEAU
     * Get user token price
     * Initially assessed by the vault owner, may be reduced during Initial Liquidity Vault Auction
     * Cannot be less than challenged price (if challenged)
     */
    function getPrice() external view returns (uint);

    /**
     * Returns remaining User Token amount of vault
     */
    function getTokenAmount() external view returns (uint);

    /**
     * Returns value of CLGN staked in EAU
     */
    function getCollateralInEau() external view returns (uint);

    function getState() external view returns (VaultState);

    /**
     * Challenge
     * Lock EAU enough to buy out all User Tokens in case of default at specified price
     * @param price - price to buy out User Tokens in attoEAU
     * @param eauToLock - EAU to lock for purchase (must be >= value of Tokens in EAU at specified price)
     */
    function challenge(uint price, uint eauToLock) external;

    /**
     * Redeem unlocked EAU and User Tokens bought
     * If User Tokens were bought during challenging or EAU tokens were unlocked, this function transfer them to
     * the challenger account.
     * Can be called only by challenger.
     * Returns EAU and User Tokens transferred to challenger
     */
    function redeemChallenge() external returns (uint eauAmount, uint userTokenAmount);

    /**
     * Returns attoEAU amount locked in challenge
     * @param challenger address
     */
    function getChallengeLocked(address challenger) external view returns (uint eauLocked);

    /**
     * Returns attoEAU and User Token amount can be redeemed
     * @param challenger address
     */
    function getRedeemableChallenge(address challenger) external view returns (uint eauAmount, uint userTokenAmount);

    /**
     * Get current challenge winner (challenger with highest price) address and price
     */
    function getChallengeWinner() external view returns (address, uint);

    event Purchase(uint amount, uint indexed price, address indexed to);
}
