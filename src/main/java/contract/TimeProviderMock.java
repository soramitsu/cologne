package contract;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
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
public class TimeProviderMock extends Contract {
    public static final String BINARY = "608060405234801561001057600080fd5b504260005560ac806100236000396000f3fe6080604052348015600f57600080fd5b506004361060325760003560e01c80633beb26c4146037578063557ed1ba146053575b600080fd5b605160048036036020811015604b57600080fd5b5035606b565b005b60596070565b60408051918252519081900360200190f35b600055565b6000549056fea2646970667358221220a55ff9e7b041ec72c02d037396a751ce2102e894a82270c5af5eb2b923fe8cfa64736f6c63430007000033";

    public static final String FUNC_GETTIME = "getTime";

    public static final String FUNC_SETTIME = "setTime";

    @Deprecated
    protected TimeProviderMock(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected TimeProviderMock(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected TimeProviderMock(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected TimeProviderMock(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<BigInteger> getTime() {
        final Function function = new Function(FUNC_GETTIME, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> setTime(BigInteger time) {
        final Function function = new Function(
                FUNC_SETTIME, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(time)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static TimeProviderMock load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new TimeProviderMock(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static TimeProviderMock load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new TimeProviderMock(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static TimeProviderMock load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new TimeProviderMock(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static TimeProviderMock load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new TimeProviderMock(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<TimeProviderMock> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(TimeProviderMock.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    public static RemoteCall<TimeProviderMock> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(TimeProviderMock.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<TimeProviderMock> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(TimeProviderMock.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<TimeProviderMock> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(TimeProviderMock.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }
}
