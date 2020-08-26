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
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
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
public class MedleyDAO extends Contract {
    public static final String BINARY = "608060405234801561001057600080fd5b506040516117983803806117988339818101604052608081101561003357600080fd5b50805160208201516040830151606090930151600280546001600160a01b03199081166001600160a01b0395861617909155600380548216938516939093179092556004805483169484169490941790935560058054909116919092161790556116f6806100a26000396000f3fe608060405234801561001057600080fd5b506004361061007d5760003560e01c8063c698a1da1161005b578063c698a1da14610136578063c8593d121461013e578063cd26be1c14610146578063f97c6e111461014e5761007d565b806350cc258e1461008257806350d23c70146100da57806377701eb01461012e575b600080fd5b61008a61017c565b60408051602080825283518183015283519192839290830191858101910280838360005b838110156100c65781810151838201526020016100ae565b505050509050019250505060405180910390f35b610112600480360360808110156100f057600080fd5b506001600160a01b0381351690602081013590604081013590606001356101de565b604080516001600160a01b039092168252519081900360200190f35b610112610464565b610112610473565b610112610482565b610112610491565b61017a6004803603604081101561016457600080fd5b506001600160a01b0381351690602001356104a0565b005b606060008054806020026020016040519081016040528092919081815260200182805480156101d457602002820191906000526020600020905b81546001600160a01b031681526001909101906020018083116101b6575b5050505050905090565b60008033858786866040516101f290610575565b80866001600160a01b03168152602001858152602001846001600160a01b0316815260200183815260200182815260200195505050505050604051809103906000f080158015610246573d6000803e3d6000fd5b50604080516323b872dd60e01b81523360048201526001600160a01b0380841660248301526044820188905291519293508892918316916323b872dd916064808201926020929091908290030181600087803b1580156102a557600080fd5b505af11580156102b9573d6000803e3d6000fd5b505050506040513d60208110156102cf57600080fd5b505161030c5760405162461bcd60e51b815260040180806020018281038252602e815260200180611693602e913960400191505060405180910390fd5b600254604080516323b872dd60e01b81523360048201526001600160a01b038581166024830152604482018a9052915191909216916323b872dd9160648083019260209291908290030181600087803b15801561036857600080fd5b505af115801561037c573d6000803e3d6000fd5b505050506040513d602081101561039257600080fd5b50516103cf5760405162461bcd60e51b815260040180806020018281038252602e815260200180611665602e913960400191505060405180910390fd5b60008054600180820183557f290decd9548b62a8d60345a988386fc84ba6bc95484008f6362f93160ef3e56390910180546001600160a01b0319166001600160a01b03861690811790915580835260208290526040808420805460ff1916909317909255905133927f9990404fa50cbe447253514fc4ab52bb578ae0b3514a50714660a1e801a2f6d391a35095945050505050565b6003546001600160a01b031690565b6004546001600160a01b031690565b6002546001600160a01b031690565b6005546001600160a01b031690565b3360009081526001602052604090205460ff16610504576040805162461bcd60e51b815260206004820152601760248201527f4f6e6c79207661756c742063616e206d696e7420454155000000000000000000604482015290519081900360640190fd5b600354604080516340c10f1960e01b81526001600160a01b03858116600483015260248201859052915191909216916340c10f1991604480830192600092919082900301818387803b15801561055957600080fd5b505af115801561056d573d6000803e3d6000fd5b505050505050565b6110e2806105838339019056fe6080604052600060075560006009556000600a553480156200002057600080fd5b50604051620010e2380380620010e2833981810160405260a08110156200004657600080fd5b5080516020808301516040808501516060860151608090960151600180546001600160a01b0319908116331791829055600280546001600160a01b03808b1691909316179055845163077701eb60e41b8152945197989597939692949116926377701eb092600480840193919291829003018186803b158015620000c957600080fd5b505afa158015620000de573d6000803e3d6000fd5b505050506040513d6020811015620000f557600080fd5b5051600380546001600160a01b0319166001600160a01b039283161790556001546040805163642c9e8960e11b81529051919092169163c8593d12916004808301926020929190829003018186803b1580156200015157600080fd5b505afa15801562000166573d6000803e3d6000fd5b505050506040513d60208110156200017d57600080fd5b5051600480546001600160a01b03199081166001600160a01b03938416179091556005805490911691851691909117905560068290556007819055620001c384620001ce565b505050505062000347565b600160009054906101000a90046001600160a01b03166001600160a01b031663c698a1da6040518163ffffffff1660e01b815260040160206040518083038186803b1580156200021d57600080fd5b505afa15801562000232573d6000803e3d6000fd5b505050506040513d60208110156200024957600080fd5b50516001546040805163642c9e8960e11b815290516001600160a01b0393841693633ddac95393169163c8593d12916004808301926020929190829003018186803b1580156200029857600080fd5b505afa158015620002ad573d6000803e3d6000fd5b505050506040513d6020811015620002c457600080fd5b5051604080516001600160e01b031960e085901b1681526001600160a01b03909216600483015260248201859052516044808301926020929190829003018186803b1580156200031357600080fd5b505afa15801562000328573d6000803e3d6000fd5b505050506040513d60208110156200033f57600080fd5b505160085550565b610d8b80620003576000396000f3fe608060405234801561001057600080fd5b506004361061009e5760003560e01c80638119c065116100665780638119c065146100a357806398d5fdca146100f9578063a69bdf1614610101578063c5ebeaec14610109578063f78be85a146101265761009e565b80631865c57d146100a3578063243582ff146100ad5780632da25de3146100a357806343d726d6146100a3578063486501c5146100ca575b600080fd5b6100ab61012e565b005b6100ab600480360360208110156100c357600080fd5b5035610130565b6100e7600480360360208110156100e057600080fd5b50356102ba565b60408051918252519081900360200190f35b6100e76102d9565b6100e76102e0565b6100ab6004803603602081101561011f57600080fd5b50356102e6565b6100e7610428565b565b600354604080516323b872dd60e01b81523360048201523060248201526044810184905290516001600160a01b03909216916323b872dd916064808201926020929091908290030181600087803b15801561018a57600080fd5b505af115801561019e573d6000803e3d6000fd5b505050506040513d60208110156101b457600080fd5b5051610207576040805162461bcd60e51b815260206004820152601b60248201527f5661756c743a2063616e6e6f74207472616e73666572204541552e0000000000604482015290519081900360640190fd5b61021360018242610481565b42600b55600061022282610486565b9050600060095482111561024457506009805460009091559081900390610250565b50600980548290039055805b60035460408051630852cd8d60e31b81526004810184905290516001600160a01b03909216916342966c689160248082019260009290919082900301818387803b15801561029d57600080fd5b505af11580156102b1573d6000803e3d6000fd5b50505050505050565b60006102d16102c8836109d1565b60095490610a8d565b90505b919050565b6007545b90565b60095490565b6002546001600160a01b0316331461033d576040805162461bcd60e51b81526020600482015260156024820152744f6e6c79206f776e65722063616e20626f72726f7760581b604482015290519081900360640190fd5b610345610428565b811115610399576040805162461bcd60e51b815260206004820152601a60248201527f437265646974206c696d69742069732065786861757374656420000000000000604482015290519081900360640190fd5b6001546002546040805163f97c6e1160e01b81526001600160a01b039283166004820152602481018590529051919092169163f97c6e1191604480830192600092919082900301818387803b1580156103f157600080fd5b505af1158015610405573d6000803e3d6000fd5b50506009805484019055505042600b819055610425906002908390610481565b50565b600080610434426102ba565b9050600061045a6004610454600754600654610af090919063ffffffff16565b90610b49565b905081811161046e576000925050506102dd565b6104788183610b8b565b925050506102dd565b505050565b600080610492426109d1565b90506000839250818311156104b257506000600a559081900390806104bd565b50818103600a556000915b60006104ca826002610b49565b905060006105d8600a6104546009600160009054906101000a90046001600160a01b03166001600160a01b031663c698a1da6040518163ffffffff1660e01b815260040160206040518083038186803b15801561052657600080fd5b505afa15801561053a573d6000803e3d6000fd5b505050506040513d602081101561055057600080fd5b505160035460408051633ddac95360e01b81526001600160a01b039283166004820152602481018a905290519190921691633ddac953916044808301926020929190829003018186803b1580156105a657600080fd5b505afa1580156105ba573d6000803e3d6000fd5b505050506040513d60208110156105d057600080fd5b505190610af0565b604080516002808252606080830184529394509091602083019080368337505060035482519293506001600160a01b03169183915060009061061657fe5b6001600160a01b03928316602091820292909201015260045482519116908290600190811061064157fe5b6001600160a01b0392831660209182029290920181019190915260015460408051633349af8760e21b8152905161271042019460609493169263cd26be1c9260048082019391829003018186803b15801561069b57600080fd5b505afa1580156106af573d6000803e3d6000fd5b505050506040513d60208110156106c557600080fd5b50516040516338ed173960e01b8152600481018781526024820187905230606483018190526084830186905260a060448401908152875160a485015287516001600160a01b03909516946338ed1739948b948b948b9490938b9360c401906020878101910280838360005b83811015610748578181015183820152602001610730565b505050509050019650505050505050600060405180830381600087803b15801561077157600080fd5b505af1158015610785573d6000803e3d6000fd5b505050506040513d6000823e601f3d908101601f1916820160405260208110156107ae57600080fd5b81019080805160405193929190846401000000008211156107ce57600080fd5b9083019060208201858111156107e357600080fd5b825186602082028301116401000000008211171561080057600080fd5b82525081516020918201928201910280838360005b8381101561082d578181015183820152602001610815565b50505050905001604052505050905060008160018151811061084b57fe5b602002602001015190508160008151811061086257fe5b602002602001015186146108a75760405162461bcd60e51b8152600401808060200182810382526039815260200180610d1d6039913960400191505060405180910390fd5b808510156108e65760405162461bcd60e51b8152600401808060200182810382526032815260200180610ceb6032913960400191505060405180910390fd5b6004805460408051630852cd8d60e31b8152928301849052516001600160a01b03909116916342966c6891602480830192600092919082900301818387803b15801561093157600080fd5b505af1158015610945573d6000803e3d6000fd5b5050600354604080516391c05b0b60e01b81528a8c03600482015290516001600160a01b0390921693506391c05b0b92506024808201926020929091908290030181600087803b15801561099857600080fd5b505af11580156109ac573d6000803e3d6000fd5b505050506040513d60208110156109c257600080fd5b50505050505050505050919050565b6000600b54600014156109e6575060006102d4565b600b54821015610a3d576040805162461bcd60e51b815260206004820181905260248201527f43616e6e6f742063616c63756c6174652066656520696e207468652070617374604482015290519081900360640190fd5b620151806000610a52620186a061016d610b49565b600a54600b54919250905b85811015610a8457620f42408383600954010281610a7757fe5b0491909101908301610a5d565b50949350505050565b600082820183811015610ae7576040805162461bcd60e51b815260206004820152601b60248201527f536166654d6174683a206164646974696f6e206f766572666c6f770000000000604482015290519081900360640190fd5b90505b92915050565b600082610aff57506000610aea565b82820282848281610b0c57fe5b0414610ae75760405162461bcd60e51b8152600401808060200182810382526021815260200180610cca6021913960400191505060405180910390fd5b6000610ae783836040518060400160405280601a81526020017f536166654d6174683a206469766973696f6e206279207a65726f000000000000815250610bcd565b6000610ae783836040518060400160405280601e81526020017f536166654d6174683a207375627472616374696f6e206f766572666c6f770000815250610c6f565b60008183610c595760405162461bcd60e51b81526004018080602001828103825283818151815260200191508051906020019080838360005b83811015610c1e578181015183820152602001610c06565b50505050905090810190601f168015610c4b5780820380516001836020036101000a031916815260200191505b509250505060405180910390fd5b506000838581610c6557fe5b0495945050505050565b60008184841115610cc15760405162461bcd60e51b8152602060048201818152835160248401528351909283926044909101919085019080838360008315610c1e578181015183820152602001610c06565b50505090039056fe536166654d6174683a206d756c7469706c69636174696f6e206f766572666c6f775661756c743a3a7061794f666628293a204d444c5920626f75676874206973206c657373207468616e2065787065637465645661756c743a3a7061794f666628293a206e6f7420657861637420616d6f756e74206f662045415520736f6c6420746f20627579204d444c59a264697066735822122093e21f1716d24fc9966ab012212fa0272ef3d71bcf39ab1dff4bbba8745f4a8d64736f6c634300070000334d65646c657944414f3a205472616e73666572206f66204d444c5920746f6b656e73206e6f7420616c6c6f7765644d65646c657944414f3a205472616e73666572206f66207573657220746f6b656e73206e6f7420616c6c6f776564a2646970667358221220636411928ecf24a3dce0a2d8373100e8170255760a38e818d2cb8e03908c7dac64736f6c63430007000033";

    public static final String FUNC_CREATEVAULT = "createVault";

    public static final String FUNC_GETEAUTOKENADDRESS = "getEauTokenAddress";

    public static final String FUNC_GETMDLYMARKET = "getMdlyMarket";

    public static final String FUNC_GETMDLYPRICEORACLE = "getMdlyPriceOracle";

    public static final String FUNC_GETMDLYTOKENADDRESS = "getMdlyTokenAddress";

    public static final String FUNC_LISTVAULTS = "listVaults";

    public static final String FUNC_MINTEAU = "mintEAU";

    public static final Event VAULTCREATION_EVENT = new Event("VaultCreation", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    ;

    @Deprecated
    protected MedleyDAO(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected MedleyDAO(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected MedleyDAO(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected MedleyDAO(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public List<VaultCreationEventResponse> getVaultCreationEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(VAULTCREATION_EVENT, transactionReceipt);
        ArrayList<VaultCreationEventResponse> responses = new ArrayList<VaultCreationEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            VaultCreationEventResponse typedResponse = new VaultCreationEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.vault = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.owner = (String) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<VaultCreationEventResponse> vaultCreationEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, VaultCreationEventResponse>() {
            @Override
            public VaultCreationEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(VAULTCREATION_EVENT, log);
                VaultCreationEventResponse typedResponse = new VaultCreationEventResponse();
                typedResponse.log = log;
                typedResponse.vault = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.owner = (String) eventValues.getIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<VaultCreationEventResponse> vaultCreationEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(VAULTCREATION_EVENT));
        return vaultCreationEventFlowable(filter);
    }

    public RemoteFunctionCall<TransactionReceipt> createVault(String token, BigInteger stake, BigInteger initialAmount, BigInteger tokenPrice) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_CREATEVAULT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, token), 
                new org.web3j.abi.datatypes.generated.Uint256(stake), 
                new org.web3j.abi.datatypes.generated.Uint256(initialAmount), 
                new org.web3j.abi.datatypes.generated.Uint256(tokenPrice)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> getEauTokenAddress() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETEAUTOKENADDRESS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> getMdlyMarket() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETMDLYMARKET, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> getMdlyPriceOracle() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETMDLYPRICEORACLE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> getMdlyTokenAddress() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETMDLYTOKENADDRESS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<List> listVaults() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_LISTVAULTS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Address>>() {}));
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

    public RemoteFunctionCall<TransactionReceipt> mintEAU(String beneficiary, BigInteger amount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_MINTEAU, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, beneficiary), 
                new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static MedleyDAO load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new MedleyDAO(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static MedleyDAO load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new MedleyDAO(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static MedleyDAO load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new MedleyDAO(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static MedleyDAO load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new MedleyDAO(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<MedleyDAO> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider, String mdlyToken, String eauToken, String mdlyPriceOracle, String mdlyMarket) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, mdlyToken), 
                new org.web3j.abi.datatypes.Address(160, eauToken), 
                new org.web3j.abi.datatypes.Address(160, mdlyPriceOracle), 
                new org.web3j.abi.datatypes.Address(160, mdlyMarket)));
        return deployRemoteCall(MedleyDAO.class, web3j, credentials, contractGasProvider, BINARY, encodedConstructor);
    }

    public static RemoteCall<MedleyDAO> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider, String mdlyToken, String eauToken, String mdlyPriceOracle, String mdlyMarket) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, mdlyToken), 
                new org.web3j.abi.datatypes.Address(160, eauToken), 
                new org.web3j.abi.datatypes.Address(160, mdlyPriceOracle), 
                new org.web3j.abi.datatypes.Address(160, mdlyMarket)));
        return deployRemoteCall(MedleyDAO.class, web3j, transactionManager, contractGasProvider, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<MedleyDAO> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String mdlyToken, String eauToken, String mdlyPriceOracle, String mdlyMarket) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, mdlyToken), 
                new org.web3j.abi.datatypes.Address(160, eauToken), 
                new org.web3j.abi.datatypes.Address(160, mdlyPriceOracle), 
                new org.web3j.abi.datatypes.Address(160, mdlyMarket)));
        return deployRemoteCall(MedleyDAO.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<MedleyDAO> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String mdlyToken, String eauToken, String mdlyPriceOracle, String mdlyMarket) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, mdlyToken), 
                new org.web3j.abi.datatypes.Address(160, eauToken), 
                new org.web3j.abi.datatypes.Address(160, mdlyPriceOracle), 
                new org.web3j.abi.datatypes.Address(160, mdlyMarket)));
        return deployRemoteCall(MedleyDAO.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static class VaultCreationEventResponse extends BaseEventResponse {
        public String vault;

        public String owner;
    }
}
