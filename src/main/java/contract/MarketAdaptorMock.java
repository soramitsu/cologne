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
public class MarketAdaptorMock extends Contract {

    public static final String BINARY = "60806040526002805534801561001457600080fd5b5060405161115a38038061115a8339818101604052604081101561003757600080fd5b508051602090910151600080546001600160a01b039384166001600160a01b031991821617909155600180549390921692169190911790556110dc8061007e6000396000f3fe608060405234801561001057600080fd5b506004361061004c5760003560e01c80631f00ca741461005157806338ed1739146101495780638803dbee146101d2578063d06ca61f1461025b575b600080fd5b6100f96004803603604081101561006757600080fd5b81359190810190604081016020820135600160201b81111561008857600080fd5b82018360208201111561009a57600080fd5b803590602001918460208302840111600160201b831117156100bb57600080fd5b919080806020026020016040519081016040528093929190818152602001838360200280828437600092019190915250929550610303945050505050565b60408051602080825283518183015283519192839290830191858101910280838360005b8381101561013557818101518382015260200161011d565b505050509050019250505060405180910390f35b6100f9600480360360a081101561015f57600080fd5b813591602081013591810190606081016040820135600160201b81111561018557600080fd5b82018360208201111561019757600080fd5b803590602001918460208302840111600160201b831117156101b857600080fd5b91935091506001600160a01b038135169060200135610556565b6100f9600480360360a08110156101e857600080fd5b813591602081013591810190606081016040820135600160201b81111561020e57600080fd5b82018360208201111561022057600080fd5b803590602001918460208302840111600160201b8311171561024157600080fd5b91935091506001600160a01b038135169060200135610b70565b6100f96004803603604081101561027157600080fd5b81359190810190604081016020820135600160201b81111561029257600080fd5b8201836020820111156102a457600080fd5b803590602001918460208302840111600160201b831117156102c557600080fd5b919080806020026020016040519081016040528093929190818152602001838360200280828437600092019190915250929550610d48945050505050565b60608151600214801561037357506000805483516001600160a01b0390911691849161032b57fe5b60200260200101516001600160a01b031614801561037357506001805483516001600160a01b03909116918491811061036057fe5b60200260200101516001600160a01b0316145b806103df575060015482516001600160a01b0390911690839060009061039557fe5b60200260200101516001600160a01b03161480156103df575060005482516001600160a01b0390911690839060019081106103cc57fe5b60200260200101516001600160a01b0316145b610426576040805162461bcd60e51b8152602060048201526013602482015272151bdad95b881b9bdd081cdd5c1c1bdc9d1959606a1b604482015290519081900360640190fd5b6040805160028082526060808301845292602083019080368337019050509050838160018151811061045457fe5b60209081029190910101526000805484516001600160a01b0390911691859161047957fe5b60200260200101516001600160a01b031614156104d7576104b9600254826001815181106104a357fe5b6020026020010151610f4890919063ffffffff16565b816000815181106104c657fe5b60200260200101818152505061054d565b60015483516001600160a01b039091169084906000906104f357fe5b60200260200101516001600160a01b0316141561054d576105336002548260018151811061051d57fe5b6020026020010151610f8a90919063ffffffff16565b8160008151811061054057fe5b6020026020010181815250505b90505b92915050565b60606002841480156105d35750600080546001600160a01b031690869086908161057c57fe5b905060200201356001600160a01b03166001600160a01b03161480156105d35750600180546001600160a01b031690869086908181106105b857fe5b905060200201356001600160a01b03166001600160a01b0316145b8061064557506001546001600160a01b031685856000816105f057fe5b905060200201356001600160a01b03166001600160a01b031614801561064557506000546001600160a01b03168585600181811061062a57fe5b905060200201356001600160a01b03166001600160a01b0316145b61068c576040805162461bcd60e51b8152602060048201526013602482015272151bdad95b881b9bdd081cdd5c1c1bdc9d1959606a1b604482015290519081900360640190fd5b60606106cb88878780806020026020016040519081016040528093929190818152602001838360200280828437600092019190915250610d4892505050565b905086816001815181106106db57fe5b6020026020010151101561072f576040805162461bcd60e51b8152602060048201526016602482015275616d6f756e744f75744d696e20746f6f206c6172676560501b604482015290519081900360640190fd5b600080546001600160a01b031690879087908161074857fe5b905060200201356001600160a01b03166001600160a01b031614156109525760008054906101000a90046001600160a01b03166001600160a01b03166323b872dd85308460008151811061079857fe5b60200260200101516040518463ffffffff1660e01b815260040180846001600160a01b03168152602001836001600160a01b031681526020018281526020019350505050602060405180830381600087803b1580156107f657600080fd5b505af115801561080a573d6000803e3d6000fd5b505050506040513d602081101561082057600080fd5b5051610869576040805162461bcd60e51b815260206004820152601360248201527226a2262c903a3930b739b332b91032b93937b960691b604482015290519081900360640190fd5b6001805482516001600160a01b039091169163a9059cbb9187918591811061088d57fe5b60200260200101516040518363ffffffff1660e01b815260040180836001600160a01b0316815260200182815260200192505050602060405180830381600087803b1580156108db57600080fd5b505af11580156108ef573d6000803e3d6000fd5b505050506040513d602081101561090557600080fd5b505161094d576040805162461bcd60e51b815260206004820152601260248201527122a0aa903a3930b739b332b91032b93937b960711b604482015290519081900360640190fd5b610b65565b6001546001600160a01b0316868660008161096957fe5b905060200201356001600160a01b03166001600160a01b03161415610b655760015481516001600160a01b03909116906323b872dd908690309085906000906109ae57fe5b60200260200101516040518463ffffffff1660e01b815260040180846001600160a01b03168152602001836001600160a01b031681526020018281526020019350505050602060405180830381600087803b158015610a0c57600080fd5b505af1158015610a20573d6000803e3d6000fd5b505050506040513d6020811015610a3657600080fd5b5051610a7e576040805162461bcd60e51b815260206004820152601260248201527122a0aa903a3930b739b332b91032b93937b960711b604482015290519081900360640190fd5b60005481516001600160a01b039091169063a9059cbb90869084906001908110610aa457fe5b60200260200101516040518363ffffffff1660e01b815260040180836001600160a01b0316815260200182815260200192505050602060405180830381600087803b158015610af257600080fd5b505af1158015610b06573d6000803e3d6000fd5b505050506040513d6020811015610b1c57600080fd5b5051610b65576040805162461bcd60e51b815260206004820152601360248201527226a2262c903a3930b739b332b91032b93937b960691b604482015290519081900360640190fd5b979650505050505050565b6060600284148015610bed5750600080546001600160a01b0316908690869081610b9657fe5b905060200201356001600160a01b03166001600160a01b0316148015610bed5750600180546001600160a01b03169086908690818110610bd257fe5b905060200201356001600160a01b03166001600160a01b0316145b80610c5f57506001546001600160a01b03168585600081610c0a57fe5b905060200201356001600160a01b03166001600160a01b0316148015610c5f57506000546001600160a01b031685856001818110610c4457fe5b905060200201356001600160a01b03166001600160a01b0316145b610ca6576040805162461bcd60e51b8152602060048201526013602482015272151bdad95b881b9bdd081cdd5c1c1bdc9d1959606a1b604482015290519081900360640190fd5b6060610ce58887878080602002602001604051908101604052809392919081815260200183836020028082843760009201919091525061030392505050565b90508681600081518110610cf557fe5b6020026020010151111561072f576040805162461bcd60e51b8152602060048201526015602482015274185b5bdd5b9d125b93585e081d1bdbc81cdb585b1b605a1b604482015290519081900360640190fd5b606081516002148015610db857506000805483516001600160a01b03909116918491610d7057fe5b60200260200101516001600160a01b0316148015610db857506001805483516001600160a01b039091169184918110610da557fe5b60200260200101516001600160a01b0316145b80610e24575060015482516001600160a01b03909116908390600090610dda57fe5b60200260200101516001600160a01b0316148015610e24575060005482516001600160a01b039091169083906001908110610e1157fe5b60200260200101516001600160a01b0316145b610e6b576040805162461bcd60e51b8152602060048201526013602482015272151bdad95b881b9bdd081cdd5c1c1bdc9d1959606a1b604482015290519081900360640190fd5b60408051600280825260608083018452926020830190803683370190505090508381600081518110610e9957fe5b60209081029190910101526000805484516001600160a01b03909116918591610ebe57fe5b60200260200101516001600160a01b03161415610ef557610ee86002548260008151811061051d57fe5b816001815181106104c657fe5b60015483516001600160a01b03909116908490600090610f1157fe5b60200260200101516001600160a01b0316141561054d57610f3b600254826000815181106104a357fe5b8160018151811061054057fe5b600061054d83836040518060400160405280601a81526020017f536166654d6174683a206469766973696f6e206279207a65726f000000000000815250610fe3565b600082610f9957506000610550565b82820282848281610fa657fe5b041461054d5760405162461bcd60e51b81526004018080602001828103825260218152602001806110866021913960400191505060405180910390fd5b6000818361106f5760405162461bcd60e51b81526004018080602001828103825283818151815260200191508051906020019080838360005b8381101561103457818101518382015260200161101c565b50505050905090810190601f1680156110615780820380516001836020036101000a031916815260200191505b509250505060405180910390fd5b50600083858161107b57fe5b049594505050505056fe536166654d6174683a206d756c7469706c69636174696f6e206f766572666c6f77a264697066735822122009fa6e006a3e0c0ac1b9af82dad105b9d5fa7a35eebe654d8f4f61b50b07830864736f6c63430007000033";

    public static final String FUNC_GETAMOUNTSIN = "getAmountsIn";

    public static final String FUNC_GETAMOUNTSOUT = "getAmountsOut";

    public static final String FUNC_SWAPEXACTTOKENSFORTOKENS = "swapExactTokensForTokens";

    public static final String FUNC_SWAPTOKENSFOREXACTTOKENS = "swapTokensForExactTokens";

    @Deprecated
    protected MarketAdaptorMock(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected MarketAdaptorMock(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected MarketAdaptorMock(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected MarketAdaptorMock(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
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

    public RemoteFunctionCall<TransactionReceipt> swapExactTokensForTokens(BigInteger amountIn, BigInteger amountOutMin, List<String> path, String to, BigInteger param4) {
        final Function function = new Function(
                FUNC_SWAPEXACTTOKENSFORTOKENS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(amountIn), 
                new org.web3j.abi.datatypes.generated.Uint256(amountOutMin), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.datatypes.Address.class,
                        org.web3j.abi.Utils.typeMap(path, org.web3j.abi.datatypes.Address.class)), 
                new org.web3j.abi.datatypes.Address(160, to), 
                new org.web3j.abi.datatypes.generated.Uint256(param4)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> swapTokensForExactTokens(BigInteger amountOut, BigInteger amountInMax, List<String> path, String to, BigInteger param4) {
        final Function function = new Function(
                FUNC_SWAPTOKENSFOREXACTTOKENS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(amountOut), 
                new org.web3j.abi.datatypes.generated.Uint256(amountInMax), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.datatypes.Address.class,
                        org.web3j.abi.Utils.typeMap(path, org.web3j.abi.datatypes.Address.class)), 
                new org.web3j.abi.datatypes.Address(160, to), 
                new org.web3j.abi.datatypes.generated.Uint256(param4)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static MarketAdaptorMock load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new MarketAdaptorMock(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static MarketAdaptorMock load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new MarketAdaptorMock(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static MarketAdaptorMock load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new MarketAdaptorMock(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static MarketAdaptorMock load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new MarketAdaptorMock(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<MarketAdaptorMock> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider, String mdly, String eau) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, mdly), 
                new org.web3j.abi.datatypes.Address(160, eau)));
        return deployRemoteCall(MarketAdaptorMock.class, web3j, credentials, contractGasProvider, BINARY, encodedConstructor);
    }

    public static RemoteCall<MarketAdaptorMock> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider, String mdly, String eau) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, mdly), 
                new org.web3j.abi.datatypes.Address(160, eau)));
        return deployRemoteCall(MarketAdaptorMock.class, web3j, transactionManager, contractGasProvider, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<MarketAdaptorMock> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String mdly, String eau) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, mdly), 
                new org.web3j.abi.datatypes.Address(160, eau)));
        return deployRemoteCall(MarketAdaptorMock.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<MarketAdaptorMock> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String mdly, String eau) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, mdly), 
                new org.web3j.abi.datatypes.Address(160, eau)));
        return deployRemoteCall(MarketAdaptorMock.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }
}
