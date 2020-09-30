// SPDX-License-Identifier: MIT

pragma solidity ^0.7.0;

interface IVault {

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
     * Stake CLGN on Vault
     * @param amount - amount of CLGN to stake
     */
    function stake(uint amount) external;

    function buy(uint amount, uint maxPrice, address to) external;

    function borrow(uint amount) external;

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

    // Get amount of EAU the owner can borrow now before credit limit is exhausted
    function canBorrow() external view returns (uint);

    function getTotalDebt() external view returns (uint);

    function getPrincipal() external view returns (uint);

    /**
     * Returns fees accrued at this moment
     */
    function getFees() external view returns (uint);

    /** Returns total fees repaid by owner (used to get liquidity fee discount) */
    function getTotalFeesRepaid() external view returns (uint);

    // Returns User Token price in EAU
    function getPrice() external view returns (uint);

    // Returns remaining User Token amount of vault
    function getTokenAmount() external view returns (uint);

    function getCollateralInEau() external view returns (uint);

    function getState() external view returns (VaultState);

    /**
     * Challenge
     * Lock EAU enough to buy out all User Tokens in case of default at specified price
     * @param price - price to buy out User Tokens
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
     * Returns EAU amount locked in challenge
     * @param challenger address
     */
    function getChallengeLocked(address challenger) external view returns (uint eauLocked);

    /**
     * Returns EAU and User Token amount can be redeemed
     * @param challenger address
     */
    function getRedeemableChallenge(address challenger) external view returns (uint eauAmount, uint userTokenAmount);

    /**
     * Get current challenge winner (challenger with highest price) address and price
     */
    function getChallengeWinner() external view returns (address, uint);

    event Purchase(uint amount, uint indexed price, address indexed to);
}
