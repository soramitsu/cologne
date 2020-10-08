package contract;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.DynamicArray;
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
public class UniswapMarketAdaptor extends Contract {
    public static final String BINARY = "608060405234801561001057600080fd5b506040516107b53803806107b58339818101604052602081101561003357600080fd5b5051600080546001600160a01b039092166001600160a01b0319909216919091179055610750806100656000396000f3fe608060405234801561001057600080fd5b506004361061004c5760003560e01c80631f00ca741461005157806338ed1739146101495780638803dbee146101d2578063d06ca61f1461025b575b600080fd5b6100f96004803603604081101561006757600080fd5b81359190810190604081016020820135600160201b81111561008857600080fd5b82018360208201111561009a57600080fd5b803590602001918460208302840111600160201b831117156100bb57600080fd5b919080806020026020016040519081016040528093929190818152602001838360200280828437600092019190915250929550610303945050505050565b60408051602080825283518183015283519192839290830191858101910280838360005b8381101561013557818101518382015260200161011d565b505050509050019250505060405180910390f35b6100f9600480360360a081101561015f57600080fd5b813591602081013591810190606081016040820135600160201b81111561018557600080fd5b82018360208201111561019757600080fd5b803590602001918460208302840111600160201b831117156101b857600080fd5b91935091506001600160a01b038135169060200135610471565b6100f9600480360360a08110156101e857600080fd5b813591602081013591810190606081016040820135600160201b81111561020e57600080fd5b82018360208201111561022057600080fd5b803590602001918460208302840111600160201b8311171561024157600080fd5b91935091506001600160a01b0381351690602001356105f7565b6100f96004803603604081101561027157600080fd5b81359190810190604081016020820135600160201b81111561029257600080fd5b8201836020820111156102a457600080fd5b803590602001918460208302840111600160201b831117156102c557600080fd5b9190808060200260200160405190810160405280939291908181526020018383602002808284376000920191909152509295506106aa945050505050565b606060008054906101000a90046001600160a01b03166001600160a01b0316631f00ca7484846040518363ffffffff1660e01b81526004018083815260200180602001828103825283818151815260200191508051906020019060200280838360005b8381101561037e578181015183820152602001610366565b50505050905001935050505060006040518083038186803b1580156103a257600080fd5b505afa1580156103b6573d6000803e3d6000fd5b505050506040513d6000823e601f3d908101601f1916820160405260208110156103df57600080fd5b8101908080516040519392919084600160201b8211156103fe57600080fd5b90830190602082018581111561041357600080fd5b82518660208202830111600160201b8211171561042f57600080fd5b82525081516020918201928201910280838360005b8381101561045c578181015183820152602001610444565b50505050905001604052505050905092915050565b606060008054906101000a90046001600160a01b03166001600160a01b03166338ed17398888888888886040518763ffffffff1660e01b81526004018087815260200186815260200180602001846001600160a01b031681526020018381526020018281038252868682818152602001925060200280828437600081840152601f19601f820116905080830192505050975050505050505050600060405180830381600087803b15801561052457600080fd5b505af1158015610538573d6000803e3d6000fd5b505050506040513d6000823e601f3d908101601f19168201604052602081101561056157600080fd5b8101908080516040519392919084600160201b82111561058057600080fd5b90830190602082018581111561059557600080fd5b82518660208202830111600160201b821117156105b157600080fd5b82525081516020918201928201910280838360005b838110156105de5781810151838201526020016105c6565b5050505090500160405250505090509695505050505050565b606060008054906101000a90046001600160a01b03166001600160a01b0316638803dbee8888888888886040518763ffffffff1660e01b81526004018087815260200186815260200180602001846001600160a01b031681526020018381526020018281038252868682818152602001925060200280828437600081840152601f19601f820116905080830192505050975050505050505050600060405180830381600087803b15801561052457600080fd5b600080546040805163d06ca61f60e01b815260048101868152602482019283528551604483015285516060956001600160a01b039095169463d06ca61f9489948994939192606490910191602080870192910290819084908490831561037e57818101518382015260200161036656fea2646970667358221220bebc1e79794316e09afe9cce09647bb91cd67cda33d2167d3d8307acba1f2d7164736f6c63430007000033";

    public static final String FUNC_GETAMOUNTSIN = "getAmountsIn";

    public static final String FUNC_GETAMOUNTSOUT = "getAmountsOut";

    public static final String FUNC_SWAPEXACTTOKENSFORTOKENS = "swapExactTokensForTokens";

    public static final String FUNC_SWAPTOKENSFOREXACTTOKENS = "swapTokensForExactTokens";

    @Deprecated
    protected UniswapMarketAdaptor(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected UniswapMarketAdaptor(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected UniswapMarketAdaptor(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected UniswapMarketAdaptor(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<List> getAmountsIn(BigInteger amountOut, List<String> path) {
        final Function function = new Function(FUNC_GETAMOUNTSIN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(amountOut), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.datatypes.Address.class,
                        org.web3j.abi.Utils.typeMap(path, org.web3j.abi.datatypes.Address.class))), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Uint256>>() {}));
        return new RemoteFunctionCall<List>(function,
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteFunctionCall<List> getAmountsOut(BigInteger amountIn, List<String> path) {
        final Function function = new Function(FUNC_GETAMOUNTSOUT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(amountIn), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.datatypes.Address.class,
                        org.web3j.abi.Utils.typeMap(path, org.web3j.abi.datatypes.Address.class))), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Uint256>>() {}));
        return new RemoteFunctionCall<List>(function,
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteFunctionCall<TransactionReceipt> swapExactTokensForTokens(BigInteger amountIn, BigInteger amountOutMin, List<String> path, String to, BigInteger deadline) {
        final Function function = new Function(
                FUNC_SWAPEXACTTOKENSFORTOKENS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(amountIn), 
                new org.web3j.abi.datatypes.generated.Uint256(amountOutMin), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.datatypes.Address.class,
                        org.web3j.abi.Utils.typeMap(path, org.web3j.abi.datatypes.Address.class)), 
                new org.web3j.abi.datatypes.Address(160, to), 
                new org.web3j.abi.datatypes.generated.Uint256(deadline)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> swapTokensForExactTokens(BigInteger amountOut, BigInteger amountInMax, List<String> path, String to, BigInteger deadline) {
        final Function function = new Function(
                FUNC_SWAPTOKENSFOREXACTTOKENS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(amountOut), 
                new org.web3j.abi.datatypes.generated.Uint256(amountInMax), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.datatypes.Address.class,
                        org.web3j.abi.Utils.typeMap(path, org.web3j.abi.datatypes.Address.class)), 
                new org.web3j.abi.datatypes.Address(160, to), 
                new org.web3j.abi.datatypes.generated.Uint256(deadline)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static UniswapMarketAdaptor load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new UniswapMarketAdaptor(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static UniswapMarketAdaptor load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new UniswapMarketAdaptor(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static UniswapMarketAdaptor load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new UniswapMarketAdaptor(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static UniswapMarketAdaptor load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new UniswapMarketAdaptor(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<UniswapMarketAdaptor> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider, String router) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, router)));
        return deployRemoteCall(UniswapMarketAdaptor.class, web3j, credentials, contractGasProvider, BINARY, encodedConstructor);
    }

    public static RemoteCall<UniswapMarketAdaptor> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider, String router) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, router)));
        return deployRemoteCall(UniswapMarketAdaptor.class, web3j, transactionManager, contractGasProvider, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<UniswapMarketAdaptor> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String router) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, router)));
        return deployRemoteCall(UniswapMarketAdaptor.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<UniswapMarketAdaptor> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String router) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, router)));
        return deployRemoteCall(UniswapMarketAdaptor.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }
}
