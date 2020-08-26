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
public class Vault extends Contract {
    public static final String BINARY = "608060405260006005556000600755600060085534801561001f57600080fd5b50604051610882380380610882833981810160405260a081101561004257600080fd5b50805160208201516040830151606084015160809094015160018054336001600160a01b0319918216179091556002805482166001600160a01b03808816919091179091556003805490921690841617905560048590556005819055929391929091906100ae846100b8565b5050505050610228565b600160009054906101000a90046001600160a01b03166001600160a01b031663c698a1da6040518163ffffffff1660e01b815260040160206040518083038186803b15801561010657600080fd5b505afa15801561011a573d6000803e3d6000fd5b505050506040513d602081101561013057600080fd5b50516001546040805163642c9e8960e11b815290516001600160a01b0393841693633ddac95393169163c8593d12916004808301926020929190829003018186803b15801561017e57600080fd5b505afa158015610192573d6000803e3d6000fd5b505050506040513d60208110156101a857600080fd5b5051604080516001600160e01b031960e085901b1681526001600160a01b03909216600483015260248201859052516044808301926020929190829003018186803b1580156101f657600080fd5b505afa15801561020a573d6000803e3d6000fd5b505050506040513d602081101561022057600080fd5b505160065550565b61064b806102376000396000f3fe608060405234801561001057600080fd5b506004361061009e5760003560e01c80638119c065116100665780638119c065146100a357806398d5fdca146100f9578063a69bdf1614610101578063c5ebeaec14610109578063f78be85a146101265761009e565b80631865c57d146100a3578063243582ff146100ad5780632da25de3146100a357806343d726d6146100a3578063486501c5146100ca575b600080fd5b6100ab61012e565b005b6100ab600480360360208110156100c357600080fd5b5035610130565b6100e7600480360360208110156100e057600080fd5b5035610133565b60408051918252519081900360200190f35b6100e7610152565b6100e7610159565b6100ab6004803603602081101561011f57600080fd5b503561015f565b6100e761029e565b565b50565b600061014a610141836102f7565b600754906103b3565b90505b919050565b6005545b90565b60075490565b6002546001600160a01b031633146101b6576040805162461bcd60e51b81526020600482015260156024820152744f6e6c79206f776e65722063616e20626f72726f7760581b604482015290519081900360640190fd5b6101be61029e565b811115610212576040805162461bcd60e51b815260206004820152601a60248201527f437265646974206c696d69742069732065786861757374656420000000000000604482015290519081900360640190fd5b6001546002546040805163f97c6e1160e01b81526001600160a01b039283166004820152602481018590529051919092169163f97c6e1191604480830192600092919082900301818387803b15801561026a57600080fd5b505af115801561027e573d6000803e3d6000fd5b505060078054840190555050426009819055610130906002908390610416565b6000806102aa42610133565b905060006102d060046102ca60055460045461041b90919063ffffffff16565b90610474565b90508181116102e457600092505050610156565b6102ee81836104b6565b92505050610156565b60006009546000141561030c5750600061014d565b600954821015610363576040805162461bcd60e51b815260206004820181905260248201527f43616e6e6f742063616c63756c6174652066656520696e207468652070617374604482015290519081900360640190fd5b620151806000610378620186a061016d610474565b600854600954919250905b858110156103aa57620f4240838360075401028161039d57fe5b0491909101908301610383565b50949350505050565b60008282018381101561040d576040805162461bcd60e51b815260206004820152601b60248201527f536166654d6174683a206164646974696f6e206f766572666c6f770000000000604482015290519081900360640190fd5b90505b92915050565b505050565b60008261042a57506000610410565b8282028284828161043757fe5b041461040d5760405162461bcd60e51b81526004018080602001828103825260218152602001806105f56021913960400191505060405180910390fd5b600061040d83836040518060400160405280601a81526020017f536166654d6174683a206469766973696f6e206279207a65726f0000000000008152506104f8565b600061040d83836040518060400160405280601e81526020017f536166654d6174683a207375627472616374696f6e206f766572666c6f77000081525061059a565b600081836105845760405162461bcd60e51b81526004018080602001828103825283818151815260200191508051906020019080838360005b83811015610549578181015183820152602001610531565b50505050905090810190601f1680156105765780820380516001836020036101000a031916815260200191505b509250505060405180910390fd5b50600083858161059057fe5b0495945050505050565b600081848411156105ec5760405162461bcd60e51b8152602060048201818152835160248401528351909283926044909101919085019080838360008315610549578181015183820152602001610531565b50505090039056fe536166654d6174683a206d756c7469706c69636174696f6e206f766572666c6f77a2646970667358221220bd4915ec473162829d788aa3cf4f7be0e919240bb9f2a5bd2b285ec3b2972d0964736f6c63430007000033";

    public static final String FUNC_BORROW = "borrow";

    public static final String FUNC_CLOSE = "close";

    public static final String FUNC_GETCREDITLIMIT = "getCreditLimit";

    public static final String FUNC_GETPRICE = "getPrice";

    public static final String FUNC_GETPRINCIPAL = "getPrincipal";

    public static final String FUNC_GETSTATE = "getState";

    public static final String FUNC_GETTOTALDEBT = "getTotalDebt";

    public static final String FUNC_PAYOFF = "payOff";

    public static final String FUNC_SLASH = "slash";

    public static final String FUNC_SWAP = "swap";

    @Deprecated
    protected Vault(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Vault(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected Vault(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected Vault(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<TransactionReceipt> borrow(BigInteger amount) {
        final Function function = new Function(
                FUNC_BORROW, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> close() {
        final Function function = new Function(
                FUNC_CLOSE, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> getCreditLimit() {
        final Function function = new Function(FUNC_GETCREDITLIMIT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> getPrice() {
        final Function function = new Function(FUNC_GETPRICE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> getPrincipal() {
        final Function function = new Function(FUNC_GETPRINCIPAL, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> getTotalDebt(BigInteger time) {
        final Function function = new Function(FUNC_GETTOTALDEBT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(time)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> payOff(BigInteger amount) {
        final Function function = new Function(
                FUNC_PAYOFF, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> slash() {
        final Function function = new Function(
                FUNC_SLASH, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> swap() {
        final Function function = new Function(
                FUNC_SWAP, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static Vault load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Vault(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Vault load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Vault(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static Vault load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new Vault(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Vault load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Vault(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<Vault> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider, String owner, BigInteger stake, String token, BigInteger initialAmount, BigInteger tokenPrice) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, owner), 
                new org.web3j.abi.datatypes.generated.Uint256(stake), 
                new org.web3j.abi.datatypes.Address(160, token), 
                new org.web3j.abi.datatypes.generated.Uint256(initialAmount), 
                new org.web3j.abi.datatypes.generated.Uint256(tokenPrice)));
        return deployRemoteCall(Vault.class, web3j, credentials, contractGasProvider, BINARY, encodedConstructor);
    }

    public static RemoteCall<Vault> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider, String owner, BigInteger stake, String token, BigInteger initialAmount, BigInteger tokenPrice) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, owner), 
                new org.web3j.abi.datatypes.generated.Uint256(stake), 
                new org.web3j.abi.datatypes.Address(160, token), 
                new org.web3j.abi.datatypes.generated.Uint256(initialAmount), 
                new org.web3j.abi.datatypes.generated.Uint256(tokenPrice)));
        return deployRemoteCall(Vault.class, web3j, transactionManager, contractGasProvider, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<Vault> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String owner, BigInteger stake, String token, BigInteger initialAmount, BigInteger tokenPrice) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, owner), 
                new org.web3j.abi.datatypes.generated.Uint256(stake), 
                new org.web3j.abi.datatypes.Address(160, token), 
                new org.web3j.abi.datatypes.generated.Uint256(initialAmount), 
                new org.web3j.abi.datatypes.generated.Uint256(tokenPrice)));
        return deployRemoteCall(Vault.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<Vault> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String owner, BigInteger stake, String token, BigInteger initialAmount, BigInteger tokenPrice) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, owner), 
                new org.web3j.abi.datatypes.generated.Uint256(stake), 
                new org.web3j.abi.datatypes.Address(160, token), 
                new org.web3j.abi.datatypes.generated.Uint256(initialAmount), 
                new org.web3j.abi.datatypes.generated.Uint256(tokenPrice)));
        return deployRemoteCall(Vault.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }
}
