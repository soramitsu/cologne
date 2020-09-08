package contract;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import org.web3j.abi.FunctionEncoder;
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
public class PriceOracleMock extends Contract {
    public static final String BINARY = "6080604052600260005534801561001557600080fd5b506040516103423803806103428339818101604052604081101561003857600080fd5b508051602090910151600180546001600160a01b039384166001600160a01b031991821617909155600280549390921692169190911790556102c38061007f6000396000f3fe608060405234801561001057600080fd5b50600436106100365760003560e01c80633ddac9531461003b578063a2e6204514610079575b600080fd5b6100676004803603604081101561005157600080fd5b506001600160a01b038135169060200135610083565b60408051918252519081900360200190f35b610081610132565b005b6001546000906001600160a01b03848116911614806100af57506002546001600160a01b038481169116145b6100f0576040805162461bcd60e51b815260206004820152600d60248201526c2ab735b737bbb7103a37b5b2b760991b604482015290519081900360640190fd5b6001546001600160a01b038481169116141561011b57600054610114908390610134565b905061012c565b60005461012990839061018d565b90505b92915050565b565b6000826101435750600061012c565b8282028284828161015057fe5b04146101295760405162461bcd60e51b815260040180806020018281038252602181526020018061026d6021913960400191505060405180910390fd5b600061012983836040518060400160405280601a81526020017f536166654d6174683a206469766973696f6e206279207a65726f000000000000815250600081836102565760405162461bcd60e51b81526004018080602001828103825283818151815260200191508051906020019080838360005b8381101561021b578181015183820152602001610203565b50505050905090810190601f1680156102485780820380516001836020036101000a031916815260200191505b509250505060405180910390fd5b50600083858161026257fe5b049594505050505056fe536166654d6174683a206d756c7469706c69636174696f6e206f766572666c6f77a26469706673582212202b71e3e236ebba0bdb09b599e181e6f8c51b52b83a7c90f9e93eb522c51cd0a264736f6c63430007010033";

    public static final String FUNC_CONSULT = "consult";

    public static final String FUNC_UPDATE = "update";

    @Deprecated
    protected PriceOracleMock(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected PriceOracleMock(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected PriceOracleMock(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected PriceOracleMock(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<BigInteger> consult(String token, BigInteger amountIn) {
        final Function function = new Function(FUNC_CONSULT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, token), 
                new org.web3j.abi.datatypes.generated.Uint256(amountIn)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> update() {
        final Function function = new Function(
                FUNC_UPDATE, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static PriceOracleMock load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new PriceOracleMock(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static PriceOracleMock load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new PriceOracleMock(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static PriceOracleMock load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new PriceOracleMock(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static PriceOracleMock load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new PriceOracleMock(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<PriceOracleMock> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider, String token1, String token2) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, token1), 
                new org.web3j.abi.datatypes.Address(160, token2)));
        return deployRemoteCall(PriceOracleMock.class, web3j, credentials, contractGasProvider, BINARY, encodedConstructor);
    }

    public static RemoteCall<PriceOracleMock> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider, String token1, String token2) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, token1), 
                new org.web3j.abi.datatypes.Address(160, token2)));
        return deployRemoteCall(PriceOracleMock.class, web3j, transactionManager, contractGasProvider, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<PriceOracleMock> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String token1, String token2) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, token1), 
                new org.web3j.abi.datatypes.Address(160, token2)));
        return deployRemoteCall(PriceOracleMock.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<PriceOracleMock> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String token1, String token2) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, token1), 
                new org.web3j.abi.datatypes.Address(160, token2)));
        return deployRemoteCall(PriceOracleMock.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }
}
