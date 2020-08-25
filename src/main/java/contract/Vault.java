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
    public static final String BINARY = "608060405260006005556000600755600060085534801561001f57600080fd5b50604051610787380380610787833981810160405260a081101561004257600080fd5b50805160208201516040830151606084015160809094015160018054336001600160a01b0319918216179091556002805482166001600160a01b03808816919091179091556003805490921690841617905560048590556005819055929391929091906100ae846100b8565b5050505050610228565b600160009054906101000a90046001600160a01b03166001600160a01b031663c698a1da6040518163ffffffff1660e01b815260040160206040518083038186803b15801561010657600080fd5b505afa15801561011a573d6000803e3d6000fd5b505050506040513d602081101561013057600080fd5b50516001546040805163642c9e8960e11b815290516001600160a01b0393841693633ddac95393169163c8593d12916004808301926020929190829003018186803b15801561017e57600080fd5b505afa158015610192573d6000803e3d6000fd5b505050506040513d60208110156101a857600080fd5b5051604080516001600160e01b031960e085901b1681526001600160a01b03909216600483015260248201859052516044808301926020929190829003018186803b1580156101f657600080fd5b505afa15801561020a573d6000803e3d6000fd5b505050506040513d602081101561022057600080fd5b505160065550565b610550806102376000396000f3fe608060405234801561001057600080fd5b50600436106100885760003560e01c80638119c0651161005b5780638119c0651461008d57806398d5fdca146100b4578063c5ebeaec146100ce578063f78be85a146100eb57610088565b80631865c57d1461008d578063243582ff146100975780632da25de31461008d57806343d726d61461008d575b600080fd5b6100956100f3565b005b610095600480360360208110156100ad57600080fd5b50356100f5565b6100bc6100f8565b60408051918252519081900360200190f35b610095600480360360208110156100e457600080fd5b50356100ff565b6100bc61023e565b565b50565b6005545b90565b6002546001600160a01b03163314610156576040805162461bcd60e51b81526020600482015260156024820152744f6e6c79206f776e65722063616e20626f72726f7760581b604482015290519081900360640190fd5b61015e61023e565b8111156101b2576040805162461bcd60e51b815260206004820152601a60248201527f437265646974206c696d69742069732065786861757374656420000000000000604482015290519081900360640190fd5b6001546002546040805163f97c6e1160e01b81526001600160a01b039283166004820152602481018590529051919092169163f97c6e1191604480830192600092919082900301818387803b15801561020a57600080fd5b505af115801561021e573d6000803e3d6000fd5b5050600780548401905550504260098190556100f5906002908390610297565b60008061024a4261029c565b90506000610270600461026a6005546004546102b990919063ffffffff16565b90610319565b9050818111610284576000925050506100fc565b61028e818361035b565b925050506100fc565b505050565b60006102b36102aa8361039d565b600754906103a3565b92915050565b6000826102c8575060006102b3565b828202828482816102d557fe5b04146103125760405162461bcd60e51b81526004018080602001828103825260218152602001806104fa6021913960400191505060405180910390fd5b9392505050565b600061031283836040518060400160405280601a81526020017f536166654d6174683a206469766973696f6e206279207a65726f0000000000008152506103fd565b600061031283836040518060400160405280601e81526020017f536166654d6174683a207375627472616374696f6e206f766572666c6f77000081525061049f565b50600090565b600082820183811015610312576040805162461bcd60e51b815260206004820152601b60248201527f536166654d6174683a206164646974696f6e206f766572666c6f770000000000604482015290519081900360640190fd5b600081836104895760405162461bcd60e51b81526004018080602001828103825283818151815260200191508051906020019080838360005b8381101561044e578181015183820152602001610436565b50505050905090810190601f16801561047b5780820380516001836020036101000a031916815260200191505b509250505060405180910390fd5b50600083858161049557fe5b0495945050505050565b600081848411156104f15760405162461bcd60e51b815260206004820181815283516024840152835190928392604490910191908501908083836000831561044e578181015183820152602001610436565b50505090039056fe536166654d6174683a206d756c7469706c69636174696f6e206f766572666c6f77a26469706673582212201029230167ace3ad9f11d4454d7531049f4f877b0c9d8d8d9c62f178ed9389a764736f6c63430007000033";

    public static final String FUNC_BORROW = "borrow";

    public static final String FUNC_CLOSE = "close";

    public static final String FUNC_GETCREDITLIMIT = "getCreditLimit";

    public static final String FUNC_GETPRICE = "getPrice";

    public static final String FUNC_GETSTATE = "getState";

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
