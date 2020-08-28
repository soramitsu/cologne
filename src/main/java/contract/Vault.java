package contract;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
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
    public static final String BINARY = "6080604052600060078190556009819055600a819055600c805460ff19908116909155600d91909155600e805490911690553480156200003e57600080fd5b5060405162001a9738038062001a97833981810160405260a08110156200006457600080fd5b5080516020808301516040808501516060860151608090960151600180546001600160a01b0319908116331791829055600280546001600160a01b03808b1691909316179055845163077701eb60e41b8152945197989597939692949116926377701eb092600480840193919291829003018186803b158015620000e757600080fd5b505afa158015620000fc573d6000803e3d6000fd5b505050506040513d60208110156200011357600080fd5b5051600380546001600160a01b0319166001600160a01b039283161790556001546040805163642c9e8960e11b81529051919092169163c8593d12916004808301926020929190829003018186803b1580156200016f57600080fd5b505afa15801562000184573d6000803e3d6000fd5b505050506040513d60208110156200019b57600080fd5b5051600480546001600160a01b03199081166001600160a01b03938416179091556005805490911691851691909117905560068290556007819055620001e184620001ec565b5050505050620001f7565b600880549091019055565b61189080620002076000396000f3fe608060405234801561001057600080fd5b50600436106100a95760003560e01c806359a87bc11161007157806359a87bc11461011457806398d5fdca14610146578063a69bdf161461014e578063c5ebeaec14610156578063f7376f0c14610173578063f78be85a1461017b576100a9565b80631865c57d146100ae578063243582ff146100b85780632da25de3146100d557806343d726d6146100dd578063486501c5146100e5575b600080fd5b6100b6610183565b005b6100b6600480360360208110156100ce57600080fd5b5035610185565b6100b6610359565b6100b66103a3565b610102600480360360208110156100fb57600080fd5b503561070f565b60408051918252519081900360200190f35b6100b66004803603606081101561012a57600080fd5b50803590602081013590604001356001600160a01b031661077c565b610102610a02565b610102610a14565b6100b66004803603602081101561016c57600080fd5b5035610a68565b610102610bf4565b610102610d5e565b565b600e5460ff16156101cf576040805162461bcd60e51b815260206004820152600f60248201526e15985d5b1d081a5cc818db1bdcd959608a1b604482015290519081900360640190fd5b600354604080516323b872dd60e01b81523360048201523060248201526044810184905290516001600160a01b03909216916323b872dd916064808201926020929091908290030181600087803b15801561022957600080fd5b505af115801561023d573d6000803e3d6000fd5b505050506040513d602081101561025357600080fd5b50516102a6576040805162461bcd60e51b815260206004820152601b60248201527f5661756c743a2063616e6e6f74207472616e73666572204541552e0000000000604482015290519081900360640190fd5b6102b260018242610e03565b42600b5560006102c182610e08565b905060006009548211156102e3575060098054600090915590819003906102ef565b50600980548290039055805b60035460408051630852cd8d60e31b81526004810184905290516001600160a01b03909216916342966c689160248082019260009290919082900301818387803b15801561033c57600080fd5b505af1158015610350573d6000803e3d6000fd5b50505050505050565b600e5460ff1615610183576040805162461bcd60e51b815260206004820152600f60248201526e15985d5b1d081a5cc818db1bdcd959608a1b604482015290519081900360640190fd5b6002546001600160a01b031633146103ba57600080fd5b6103c34261070f565b156103ff5760405162461bcd60e51b815260040180806020018281038252603781526020018061172d6037913960400191505060405180910390fd5b600554600254604080516370a0823160e01b815230600482015290516001600160a01b039384169363a9059cbb93169184916370a0823191602480820192602092909190829003018186803b15801561045757600080fd5b505afa15801561046b573d6000803e3d6000fd5b505050506040513d602081101561048157600080fd5b5051604080516001600160e01b031960e086901b1681526001600160a01b03909316600484015260248301919091525160448083019260209291908290030181600087803b1580156104d257600080fd5b505af11580156104e6573d6000803e3d6000fd5b505050506040513d60208110156104fc57600080fd5b505060048054600254604080516370a0823160e01b81523094810194909452516001600160a01b039283169363a9059cbb939092169184916370a0823191602480820192602092909190829003018186803b15801561055a57600080fd5b505afa15801561056e573d6000803e3d6000fd5b505050506040513d602081101561058457600080fd5b5051604080516001600160e01b031960e086901b1681526001600160a01b03909316600484015260248301919091525160448083019260209291908290030181600087803b1580156105d557600080fd5b505af11580156105e9573d6000803e3d6000fd5b505050506040513d60208110156105ff57600080fd5b5050600354600254604080516370a0823160e01b815230600482015290516001600160a01b039384169363a9059cbb93169184916370a0823191602480820192602092909190829003018186803b15801561065957600080fd5b505afa15801561066d573d6000803e3d6000fd5b505050506040513d602081101561068357600080fd5b5051604080516001600160e01b031960e086901b1681526001600160a01b03909316600484015260248301919091525160448083019260209291908290030181600087803b1580156106d457600080fd5b505af11580156106e8573d6000803e3d6000fd5b505050506040513d60208110156106fe57600080fd5b5050600e805460ff19166001179055565b600e5460009060ff161561075c576040805162461bcd60e51b815260206004820152600f60248201526e15985d5b1d081a5cc818db1bdcd959608a1b604482015290519081900360640190fd5b61076582611354565b506009549091506107769082611476565b92915050565b600e5460ff16156107c6576040805162461bcd60e51b815260206004820152600f60248201526e15985d5b1d081a5cc818db1bdcd959608a1b604482015290519081900360640190fd5b600554604080516370a0823160e01b815230600482015290516001600160a01b03909216916370a0823191602480820192602092909190829003018186803b15801561081157600080fd5b505afa158015610825573d6000803e3d6000fd5b505050506040513d602081101561083b57600080fd5b505183111561087b5760405162461bcd60e51b81526004018080602001828103825260278152602001806118346027913960400191505060405180910390fd5b6000610885610a02565b9050828111156108dc576040805162461bcd60e51b815260206004820152601b60248201527f5661756c743a3a62757928293a20507269636520746f6f206c6f770000000000604482015290519081900360640190fd5b8381026108e881610185565b6005546040805163a9059cbb60e01b81526001600160a01b038681166004830152602482018990529151919092169163a9059cbb9160448083019260209291908290030181600087803b15801561093e57600080fd5b505af1158015610952573d6000803e3d6000fd5b505050506040513d602081101561096857600080fd5b50516109bb576040805162461bcd60e51b815260206004820181905260248201527f5661756c743a3a6275793a2063616e6e6f74207472616e73666572204541552e604482015290519081900360640190fd5b6040805186815290516001600160a01b0385169184917f9d995b79e708615dc7201d66ebdcb94d773fe76aac960305383a8b4f8dda9f059181900360200190a35050505050565b6000610a0d42611354565b9150505b90565b600e5460009060ff1615610a61576040805162461bcd60e51b815260206004820152600f60248201526e15985d5b1d081a5cc818db1bdcd959608a1b604482015290519081900360640190fd5b5060095490565b600e5460ff1615610ab2576040805162461bcd60e51b815260206004820152600f60248201526e15985d5b1d081a5cc818db1bdcd959608a1b604482015290519081900360640190fd5b6002546001600160a01b03163314610b09576040805162461bcd60e51b81526020600482015260156024820152744f6e6c79206f776e65722063616e20626f72726f7760581b604482015290519081900360640190fd5b610b11610d5e565b811115610b65576040805162461bcd60e51b815260206004820152601a60248201527f437265646974206c696d69742069732065786861757374656420000000000000604482015290519081900360640190fd5b6001546002546040805163f97c6e1160e01b81526001600160a01b039283166004820152602481018590529051919092169163f97c6e1191604480830192600092919082900301818387803b158015610bbd57600080fd5b505af1158015610bd1573d6000803e3d6000fd5b50506009805484019055505042600b819055610bf1906002908390610e03565b50565b6001546040805163634c50ed60e11b815290516000926001600160a01b03169163c698a1da916004808301926020929190829003018186803b158015610c3957600080fd5b505afa158015610c4d573d6000803e3d6000fd5b505050506040513d6020811015610c6357600080fd5b50516001546040805163642c9e8960e11b815290516001600160a01b0393841693633ddac95393169163c8593d12916004808301926020929190829003018186803b158015610cb157600080fd5b505afa158015610cc5573d6000803e3d6000fd5b505050506040513d6020811015610cdb57600080fd5b5051600854604080516001600160e01b031960e086901b1681526001600160a01b0390931660048401526024830191909152516044808301926020929190829003018186803b158015610d2d57600080fd5b505afa158015610d41573d6000803e3d6000fd5b505050506040513d6020811015610d5757600080fd5b5051905090565b600e5460009060ff1615610dab576040805162461bcd60e51b815260206004820152600f60248201526e15985d5b1d081a5cc818db1bdcd959608a1b604482015290519081900360640190fd5b6000610db64261070f565b90506000610ddc6004610dd66007546006546114d790919063ffffffff16565b90611530565b9050818111610df057600092505050610a11565b610dfa8183611572565b92505050610a11565b505050565b600080610e1442611354565b508392509050600081831115610e3557506000600a55908190039080610e40565b50818103600a556000915b6000610e4d826002611530565b90506000610f5b600a610dd66009600160009054906101000a90046001600160a01b03166001600160a01b031663c698a1da6040518163ffffffff1660e01b815260040160206040518083038186803b158015610ea957600080fd5b505afa158015610ebd573d6000803e3d6000fd5b505050506040513d6020811015610ed357600080fd5b505160035460408051633ddac95360e01b81526001600160a01b039283166004820152602481018a905290519190921691633ddac953916044808301926020929190829003018186803b158015610f2957600080fd5b505afa158015610f3d573d6000803e3d6000fd5b505050506040513d6020811015610f5357600080fd5b5051906114d7565b604080516002808252606080830184529394509091602083019080368337505060035482519293506001600160a01b031691839150600090610f9957fe5b6001600160a01b039283166020918202929092010152600454825191169082906001908110610fc457fe5b6001600160a01b0392831660209182029290920181019190915260015460408051633349af8760e21b8152905161271042019460609493169263cd26be1c9260048082019391829003018186803b15801561101e57600080fd5b505afa158015611032573d6000803e3d6000fd5b505050506040513d602081101561104857600080fd5b50516040516338ed173960e01b8152600481018781526024820187905230606483018190526084830186905260a060448401908152875160a485015287516001600160a01b03909516946338ed1739948b948b948b9490938b9360c401906020878101910280838360005b838110156110cb5781810151838201526020016110b3565b505050509050019650505050505050600060405180830381600087803b1580156110f457600080fd5b505af1158015611108573d6000803e3d6000fd5b505050506040513d6000823e601f3d908101601f19168201604052602081101561113157600080fd5b810190808051604051939291908464010000000082111561115157600080fd5b90830190602082018581111561116657600080fd5b825186602082028301116401000000008211171561118357600080fd5b82525081516020918201928201910280838360005b838110156111b0578181015183820152602001611198565b5050505090500160405250505090506000816001815181106111ce57fe5b60200260200101519050816000815181106111e557fe5b6020026020010151861461122a5760405162461bcd60e51b81526004018080602001828103825260398152602001806117fb6039913960400191505060405180910390fd5b808510156112695760405162461bcd60e51b81526004018080602001828103825260328152602001806117c96032913960400191505060405180910390fd5b6004805460408051630852cd8d60e31b8152928301849052516001600160a01b03909116916342966c6891602480830192600092919082900301818387803b1580156112b457600080fd5b505af11580156112c8573d6000803e3d6000fd5b5050600354604080516391c05b0b60e01b81528a8c03600482015290516001600160a01b0390921693506391c05b0b92506024808201926020929091908290030181600087803b15801561131b57600080fd5b505af115801561132f573d6000803e3d6000fd5b505050506040513d602081101561134557600080fd5b50505050505050505050919050565b600080600b546000141561136f575050600754600090611471565b600b548310156113c6576040805162461bcd60e51b815260206004820181905260248201527f43616e6e6f742063616c63756c6174652066656520696e207468652070617374604482015290519081900360640190fd5b6201518060006113db620186a061016d611530565b905060006113e8816115b4565b600a54600b549096509094505b8681101561146c5760006114196004610dd6886006546114d790919063ffffffff16565b9050806114318860095461147690919063ffffffff16565b111561144c5782611440578192505b611449836115b4565b95505b620f4240848860095401028161145e57fe5b0496909601955083016113f5565b505050505b915091565b6000828201838110156114d0576040805162461bcd60e51b815260206004820152601b60248201527f536166654d6174683a206164646974696f6e206f766572666c6f770000000000604482015290519081900360640190fd5b9392505050565b6000826114e657506000610776565b828202828482816114f357fe5b04146114d05760405162461bcd60e51b81526004018080602001828103825260218152602001806117646021913960400191505060405180910390fd5b60006114d083836040518060400160405280601a81526020017f536166654d6174683a206469766973696f6e206279207a65726f000000000000815250611630565b60006114d083836040518060400160405280601e81526020017f536166654d6174683a207375627472616374696f6e206f766572666c6f7700008152506116d2565b600754610708821561162a57824210156115ff5760405162461bcd60e51b81526004018080602001828103825260448152602001806117856044913960600191505060405180910390fd5b600061160f82610dd64287611572565b6065900690506116266064610dd6858483036114d7565b9250505b50919050565b600081836116bc5760405162461bcd60e51b81526004018080602001828103825283818151815260200191508051906020019080838360005b83811015611681578181015183820152602001611669565b50505050905090810190601f1680156116ae5780820380516001836020036101000a031916815260200191505b509250505060405180910390fd5b5060008385816116c857fe5b0495945050505050565b600081848411156117245760405162461bcd60e51b8152602060048201818152835160248401528351909283926044909101919085019080838360008315611681578181015183820152602001611669565b50505090039056fe5661756c743a3a636c6f736528293a20636c6f736520616c6c6f776564206f6e6c792069662064656274206973207061796564206f6666536166654d6174683a206d756c7469706c69636174696f6e206f766572666c6f775661756c743a3a676574507269636528293a20496e636f72726563742073746174653a204c696d697420697320627265616368656420696e2074686520667574757265215661756c743a3a7061794f666628293a204d444c5920626f75676874206973206c657373207468616e2065787065637465645661756c743a3a7061794f666628293a206e6f7420657861637420616d6f756e74206f662045415520736f6c6420746f20627579204d444c595661756c743a3a62757928293a204e6f7420656e6f75676820746f6b656e7320746f2073656c6ca264697066735822122062ee7c3d1f5abc6ca532e60016e90189522654b3fa62fdc5555771b7a4f5f1f764736f6c63430007000033";

    public static final String FUNC_BORROW = "borrow";

    public static final String FUNC_BUY = "buy";

    public static final String FUNC_CLOSE = "close";

    public static final String FUNC_GETCOLLATERALINEAU = "getCollateralInEau";

    public static final String FUNC_GETCREDITLIMIT = "getCreditLimit";

    public static final String FUNC_GETPRICE = "getPrice";

    public static final String FUNC_GETPRINCIPAL = "getPrincipal";

    public static final String FUNC_GETSTATE = "getState";

    public static final String FUNC_GETTOTALDEBT = "getTotalDebt";

    public static final String FUNC_PAYOFF = "payOff";

    public static final String FUNC_SLASH = "slash";

    public static final Event PURCHASE_EVENT = new Event("Purchase", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>(true) {}, new TypeReference<Address>(true) {}));
    ;

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

    public RemoteFunctionCall<TransactionReceipt> close() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_CLOSE, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
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

    public RemoteFunctionCall<BigInteger> getTotalDebt(BigInteger time) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETTOTALDEBT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(time)), 
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

    public RemoteFunctionCall<TransactionReceipt> slash() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_SLASH, 
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

    public static class PurchaseEventResponse extends BaseEventResponse {
        public BigInteger price;

        public String to;

        public BigInteger amount;
    }
}
