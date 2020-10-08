package contract;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.5.16.
 */
@SuppressWarnings("rawtypes")
public class IVault extends Contract {
    public static final String BINARY = "";

    public static final String FUNC_BORROW = "borrow";

    public static final String FUNC_BUY = "buy";

    public static final String FUNC_CANBORROW = "canBorrow";

    public static final String FUNC_CHALLENGE = "challenge";

    public static final String FUNC_CLOSE = "close";

    public static final String FUNC_COVERSHORTFALL = "coverShortfall";

    public static final String FUNC_GETCHALLENGELOCKED = "getChallengeLocked";

    public static final String FUNC_GETCHALLENGEWINNER = "getChallengeWinner";

    public static final String FUNC_GETCOLLATERALINEAU = "getCollateralInEau";

    public static final String FUNC_GETCREDITLIMIT = "getCreditLimit";

    public static final String FUNC_GETFEERATE = "getFeeRate";

    public static final String FUNC_GETFEES = "getFees";

    public static final String FUNC_GETPRICE = "getPrice";

    public static final String FUNC_GETPRINCIPAL = "getPrincipal";

    public static final String FUNC_GETREDEEMABLECHALLENGE = "getRedeemableChallenge";

    public static final String FUNC_GETSTATE = "getState";

    public static final String FUNC_GETTOKENAMOUNT = "getTokenAmount";

    public static final String FUNC_GETTOTALDEBT = "getTotalDebt";

    public static final String FUNC_GETTOTALFEESREPAID = "getTotalFeesRepaid";

    public static final String FUNC_PAYOFF = "payOff";

    public static final String FUNC_REDEEMCHALLENGE = "redeemChallenge";

    public static final String FUNC_SLASH = "slash";

    public static final String FUNC_STAKE = "stake";

    public static final String FUNC_STARTINITIALLIQUIDITYAUCTION = "startInitialLiquidityAuction";

    public static final Event PURCHASE_EVENT = new Event("Purchase", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>(true) {}, new TypeReference<Address>(true) {}));
    ;

    @Deprecated
    protected IVault(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected IVault(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected IVault(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected IVault(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public List<PurchaseEventResponse> getPurchaseEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(PURCHASE_EVENT, transactionReceipt);
        ArrayList<PurchaseEventResponse> responses = new ArrayList<PurchaseEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            PurchaseEventResponse typedResponse = new PurchaseEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.price = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<PurchaseEventResponse> purchaseEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, PurchaseEventResponse>() {
            @Override
            public PurchaseEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(PURCHASE_EVENT, log);
                PurchaseEventResponse typedResponse = new PurchaseEventResponse();
                typedResponse.log = log;
                typedResponse.price = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<PurchaseEventResponse> purchaseEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(PURCHASE_EVENT));
        return purchaseEventFlowable(filter);
    }

    public RemoteFunctionCall<TransactionReceipt> borrow(BigInteger amount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_BORROW, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> buy(BigInteger amount, BigInteger maxPrice, String to) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_BUY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(amount), 
                new org.web3j.abi.datatypes.generated.Uint256(maxPrice), 
                new org.web3j.abi.datatypes.Address(160, to)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> canBorrow() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_CANBORROW, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> challenge(BigInteger price, BigInteger eauToLock) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_CHALLENGE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(price), 
                new org.web3j.abi.datatypes.generated.Uint256(eauToLock)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> close() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_CLOSE, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> coverShortfall() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_COVERSHORTFALL, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> getChallengeLocked(String challenger) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETCHALLENGELOCKED, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, challenger)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<Tuple2<String, BigInteger>> getChallengeWinner() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETCHALLENGEWINNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple2<String, BigInteger>>(function,
                new Callable<Tuple2<String, BigInteger>>() {
                    @Override
                    public Tuple2<String, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<String, BigInteger>(
                                (String) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue());
                    }
                });
    }

    public RemoteFunctionCall<BigInteger> getCollateralInEau() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETCOLLATERALINEAU, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> getCreditLimit() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETCREDITLIMIT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> getFeeRate() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETFEERATE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> getFees() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETFEES, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> getPrice() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETPRICE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> getPrincipal() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETPRINCIPAL, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<Tuple2<BigInteger, BigInteger>> getRedeemableChallenge(String challenger) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETREDEEMABLECHALLENGE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, challenger)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple2<BigInteger, BigInteger>>(function,
                new Callable<Tuple2<BigInteger, BigInteger>>() {
                    @Override
                    public Tuple2<BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<BigInteger, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue());
                    }
                });
    }

    public RemoteFunctionCall<BigInteger> getState() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETSTATE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> getTokenAmount() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETTOKENAMOUNT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> getTotalDebt() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETTOTALDEBT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> getTotalFeesRepaid() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETTOTALFEESREPAID, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> payOff(BigInteger amount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_PAYOFF, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> redeemChallenge() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_REDEEMCHALLENGE, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> slash() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_SLASH, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> stake(BigInteger amount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_STAKE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> startInitialLiquidityAuction() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_STARTINITIALLIQUIDITYAUCTION, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static IVault load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new IVault(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static IVault load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new IVault(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static IVault load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new IVault(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static IVault load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new IVault(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<IVault> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(IVault.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<IVault> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(IVault.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<IVault> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(IVault.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<IVault> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(IVault.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static class PurchaseEventResponse extends BaseEventResponse {
        public BigInteger price;

        public String to;

        public BigInteger amount;
    }
}
