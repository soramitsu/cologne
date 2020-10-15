package contract;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
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
public class CLGNToken extends Contract {
    public static final String BINARY = "60806040523480156200001157600080fd5b506200001c62000123565b604080518082018252600f81526e2a32b9ba1021a623a7103a37b5b2b760891b6020808301918252835180850190945260098452682a22a9aa2fa1a623a760b91b9084015281519192916200007491600391620002ba565b5080516200008a906004906020840190620002ba565b505060058054601260ff1990911617610100600160a81b0319166101006001600160a01b038581168202929092179283905560405192041691506000907f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e0908290a3506200011d620000fb62000127565b620001056200013b565b60ff16600a0a63015752a0026200014460201b60201c565b62000356565b3390565b60055461010090046001600160a01b031690565b60055460ff1690565b6001600160a01b038216620001a0576040805162461bcd60e51b815260206004820152601f60248201527f45524332303a206d696e7420746f20746865207a65726f206164647265737300604482015290519081900360640190fd5b620001ae6000838362000253565b620001ca816002546200025860201b620008bb1790919060201c565b6002556001600160a01b03821660009081526020818152604090912054620001fd918390620008bb62000258821b17901c565b6001600160a01b0383166000818152602081815260408083209490945583518581529351929391927fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9281900390910190a35050565b505050565b600082820183811015620002b3576040805162461bcd60e51b815260206004820152601b60248201527f536166654d6174683a206164646974696f6e206f766572666c6f770000000000604482015290519081900360640190fd5b9392505050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f10620002fd57805160ff19168380011785556200032d565b828001600101855582156200032d579182015b828111156200032d57825182559160200191906001019062000310565b506200033b9291506200033f565b5090565b5b808211156200033b576000815560010162000340565b61104480620003666000396000f3fe608060405234801561001057600080fd5b50600436106101165760003560e01c8063715018a6116100a257806395d89b411161007157806395d89b4114610343578063a457c2d71461034b578063a9059cbb14610377578063dd62ed3e146103a3578063f2fde38b146103d157610116565b8063715018a6146102e357806379cc6790146102eb5780638da5cb5b146103175780638f32d59b1461033b57610116565b8063313ce567116100e9578063313ce56714610228578063395093511461024657806340c10f191461027257806342966c68146102a057806370a08231146102bd57610116565b806306fdde031461011b578063095ea7b31461019857806318160ddd146101d857806323b872dd146101f2575b600080fd5b6101236103f7565b6040805160208082528351818301528351919283929083019185019080838360005b8381101561015d578181015183820152602001610145565b50505050905090810190601f16801561018a5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b6101c4600480360360408110156101ae57600080fd5b506001600160a01b03813516906020013561048d565b604080519115158252519081900360200190f35b6101e06104aa565b60408051918252519081900360200190f35b6101c46004803603606081101561020857600080fd5b506001600160a01b038135811691602081013590911690604001356104b0565b610230610537565b6040805160ff9092168252519081900360200190f35b6101c46004803603604081101561025c57600080fd5b506001600160a01b038135169060200135610540565b61029e6004803603604081101561028857600080fd5b506001600160a01b03813516906020013561058e565b005b61029e600480360360208110156102b657600080fd5b50356105f5565b6101e0600480360360208110156102d357600080fd5b50356001600160a01b0316610609565b61029e610624565b61029e6004803603604081101561030157600080fd5b506001600160a01b0381351690602001356106cd565b61031f610727565b604080516001600160a01b039092168252519081900360200190f35b6101c461073b565b610123610751565b6101c46004803603604081101561036157600080fd5b506001600160a01b0381351690602001356107b2565b6101c46004803603604081101561038d57600080fd5b506001600160a01b03813516906020013561081a565b6101e0600480360360408110156103b957600080fd5b506001600160a01b038135811691602001351661082e565b61029e600480360360208110156103e757600080fd5b50356001600160a01b0316610859565b60038054604080516020601f60026000196101006001881615020190951694909404938401819004810282018101909252828152606093909290918301828280156104835780601f1061045857610100808354040283529160200191610483565b820191906000526020600020905b81548152906001019060200180831161046657829003601f168201915b5050505050905090565b60006104a161049a61091c565b8484610920565b50600192915050565b60025490565b60006104bd848484610a0c565b61052d846104c961091c565b61052885604051806060016040528060288152602001610f34602891396001600160a01b038a1660009081526001602052604081209061050761091c565b6001600160a01b031681526020810191909152604001600020549190610b67565b610920565b5060019392505050565b60055460ff1690565b60006104a161054d61091c565b84610528856001600061055e61091c565b6001600160a01b03908116825260208083019390935260409182016000908120918c1681529252902054906108bb565b61059661073b565b6105e7576040805162461bcd60e51b815260206004820152601e60248201527f4f6e6c79206f776e657220697320616c6c6f77656420746f20646f2069740000604482015290519081900360640190fd5b6105f18282610bfe565b5050565b61060661060061091c565b82610cee565b50565b6001600160a01b031660009081526020819052604090205490565b61062c61073b565b61067d576040805162461bcd60e51b815260206004820152601e60248201527f4f6e6c79206f776e657220697320616c6c6f77656420746f20646f2069740000604482015290519081900360640190fd5b60055460405160009161010090046001600160a01b0316907f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e0908390a360058054610100600160a81b0319169055565b600061070482604051806060016040528060248152602001610f5c602491396106fd866106f861091c565b61082e565b9190610b67565b90506107188361071261091c565b83610920565b6107228383610cee565b505050565b60055461010090046001600160a01b031690565b60055461010090046001600160a01b0316331490565b60048054604080516020601f60026000196101006001881615020190951694909404938401819004810282018101909252828152606093909290918301828280156104835780601f1061045857610100808354040283529160200191610483565b60006104a16107bf61091c565b8461052885604051806060016040528060258152602001610fea60259139600160006107e961091c565b6001600160a01b03908116825260208083019390935260409182016000908120918d16815292529020549190610b67565b60006104a161082761091c565b8484610a0c565b6001600160a01b03918216600090815260016020908152604080832093909416825291909152205490565b61086161073b565b6108b2576040805162461bcd60e51b815260206004820152601e60248201527f4f6e6c79206f776e657220697320616c6c6f77656420746f20646f2069740000604482015290519081900360640190fd5b61060681610dea565b600082820183811015610915576040805162461bcd60e51b815260206004820152601b60248201527f536166654d6174683a206164646974696f6e206f766572666c6f770000000000604482015290519081900360640190fd5b9392505050565b3390565b6001600160a01b0383166109655760405162461bcd60e51b8152600401808060200182810382526024815260200180610fc66024913960400191505060405180910390fd5b6001600160a01b0382166109aa5760405162461bcd60e51b8152600401808060200182810382526022815260200180610eec6022913960400191505060405180910390fd5b6001600160a01b03808416600081815260016020908152604080832094871680845294825291829020859055815185815291517f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b9259281900390910190a3505050565b6001600160a01b038316610a515760405162461bcd60e51b8152600401808060200182810382526025815260200180610fa16025913960400191505060405180910390fd5b6001600160a01b038216610a965760405162461bcd60e51b8152600401808060200182810382526023815260200180610ea76023913960400191505060405180910390fd5b610aa1838383610722565b610ade81604051806060016040528060268152602001610f0e602691396001600160a01b0386166000908152602081905260409020549190610b67565b6001600160a01b038085166000908152602081905260408082209390935590841681522054610b0d90826108bb565b6001600160a01b038084166000818152602081815260409182902094909455805185815290519193928716927fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef92918290030190a3505050565b60008184841115610bf65760405162461bcd60e51b81526004018080602001828103825283818151815260200191508051906020019080838360005b83811015610bbb578181015183820152602001610ba3565b50505050905090810190601f168015610be85780820380516001836020036101000a031916815260200191505b509250505060405180910390fd5b505050900390565b6001600160a01b038216610c59576040805162461bcd60e51b815260206004820152601f60248201527f45524332303a206d696e7420746f20746865207a65726f206164647265737300604482015290519081900360640190fd5b610c6560008383610722565b600254610c7290826108bb565b6002556001600160a01b038216600090815260208190526040902054610c9890826108bb565b6001600160a01b0383166000818152602081815260408083209490945583518581529351929391927fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9281900390910190a35050565b6001600160a01b038216610d335760405162461bcd60e51b8152600401808060200182810382526021815260200180610f806021913960400191505060405180910390fd5b610d3f82600083610722565b610d7c81604051806060016040528060228152602001610eca602291396001600160a01b0385166000908152602081905260409020549190610b67565b6001600160a01b038316600090815260208190526040902055600254610da29082610e64565b6002556040805182815290516000916001600160a01b038516917fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9181900360200190a35050565b6001600160a01b038116610dfd57600080fd5b6005546040516001600160a01b0380841692610100900416907f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e090600090a3600580546001600160a01b0390921661010002610100600160a81b0319909216919091179055565b600061091583836040518060400160405280601e81526020017f536166654d6174683a207375627472616374696f6e206f766572666c6f770000815250610b6756fe45524332303a207472616e7366657220746f20746865207a65726f206164647265737345524332303a206275726e20616d6f756e7420657863656564732062616c616e636545524332303a20617070726f766520746f20746865207a65726f206164647265737345524332303a207472616e7366657220616d6f756e7420657863656564732062616c616e636545524332303a207472616e7366657220616d6f756e74206578636565647320616c6c6f77616e636545524332303a206275726e20616d6f756e74206578636565647320616c6c6f77616e636545524332303a206275726e2066726f6d20746865207a65726f206164647265737345524332303a207472616e736665722066726f6d20746865207a65726f206164647265737345524332303a20617070726f76652066726f6d20746865207a65726f206164647265737345524332303a2064656372656173656420616c6c6f77616e63652062656c6f77207a65726fa2646970667358221220f0f636163207cad20a18c984410782ff778a15a0883bd9d226342e891684994864736f6c63430007010033";

    public static final String FUNC_ALLOWANCE = "allowance";

    public static final String FUNC_APPROVE = "approve";

    public static final String FUNC_BALANCEOF = "balanceOf";

    public static final String FUNC_BURN = "burn";

    public static final String FUNC_BURNFROM = "burnFrom";

    public static final String FUNC_DECIMALS = "decimals";

    public static final String FUNC_DECREASEALLOWANCE = "decreaseAllowance";

    public static final String FUNC_INCREASEALLOWANCE = "increaseAllowance";

    public static final String FUNC_ISOWNER = "isOwner";

    public static final String FUNC_MINT = "mint";

    public static final String FUNC_NAME = "name";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_RENOUNCEOWNERSHIP = "renounceOwnership";

    public static final String FUNC_SYMBOL = "symbol";

    public static final String FUNC_TOTALSUPPLY = "totalSupply";

    public static final String FUNC_TRANSFER = "transfer";

    public static final String FUNC_TRANSFERFROM = "transferFrom";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final Event APPROVAL_EVENT = new Event("Approval", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event OWNERSHIPTRANSFERRED_EVENT = new Event("OwnershipTransferred", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    ;

    public static final Event TRANSFER_EVENT = new Event("Transfer", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    @Deprecated
    protected CLGNToken(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected CLGNToken(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected CLGNToken(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected CLGNToken(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public List<ApprovalEventResponse> getApprovalEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(APPROVAL_EVENT, transactionReceipt);
        ArrayList<ApprovalEventResponse> responses = new ArrayList<ApprovalEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ApprovalEventResponse typedResponse = new ApprovalEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.spender = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<ApprovalEventResponse> approvalEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, ApprovalEventResponse>() {
            @Override
            public ApprovalEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(APPROVAL_EVENT, log);
                ApprovalEventResponse typedResponse = new ApprovalEventResponse();
                typedResponse.log = log;
                typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.spender = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<ApprovalEventResponse> approvalEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(APPROVAL_EVENT));
        return approvalEventFlowable(filter);
    }

    public List<OwnershipTransferredEventResponse> getOwnershipTransferredEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(OWNERSHIPTRANSFERRED_EVENT, transactionReceipt);
        ArrayList<OwnershipTransferredEventResponse> responses = new ArrayList<OwnershipTransferredEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<OwnershipTransferredEventResponse> ownershipTransferredEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, OwnershipTransferredEventResponse>() {
            @Override
            public OwnershipTransferredEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(OWNERSHIPTRANSFERRED_EVENT, log);
                OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
                typedResponse.log = log;
                typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<OwnershipTransferredEventResponse> ownershipTransferredEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(OWNERSHIPTRANSFERRED_EVENT));
        return ownershipTransferredEventFlowable(filter);
    }

    public List<TransferEventResponse> getTransferEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(TRANSFER_EVENT, transactionReceipt);
        ArrayList<TransferEventResponse> responses = new ArrayList<TransferEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TransferEventResponse typedResponse = new TransferEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<TransferEventResponse> transferEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, TransferEventResponse>() {
            @Override
            public TransferEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(TRANSFER_EVENT, log);
                TransferEventResponse typedResponse = new TransferEventResponse();
                typedResponse.log = log;
                typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<TransferEventResponse> transferEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TRANSFER_EVENT));
        return transferEventFlowable(filter);
    }

    public RemoteFunctionCall<BigInteger> allowance(String owner, String spender) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_ALLOWANCE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, owner), 
                new org.web3j.abi.datatypes.Address(160, spender)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> approve(String spender, BigInteger amount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_APPROVE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, spender), 
                new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> balanceOf(String account) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_BALANCEOF, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, account)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> burn(BigInteger amount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_BURN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> burnFrom(String account, BigInteger amount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_BURNFROM, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, account), 
                new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> decimals() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_DECIMALS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> decreaseAllowance(String spender, BigInteger subtractedValue) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_DECREASEALLOWANCE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, spender), 
                new org.web3j.abi.datatypes.generated.Uint256(subtractedValue)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> increaseAllowance(String spender, BigInteger addedValue) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_INCREASEALLOWANCE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, spender), 
                new org.web3j.abi.datatypes.generated.Uint256(addedValue)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Boolean> isOwner() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_ISOWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<TransactionReceipt> mint(String account, BigInteger value) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_MINT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, account), 
                new org.web3j.abi.datatypes.generated.Uint256(value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> name() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_NAME, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> owner() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_OWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> renounceOwnership() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_RENOUNCEOWNERSHIP, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> symbol() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_SYMBOL, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> totalSupply() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_TOTALSUPPLY, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> transfer(String recipient, BigInteger amount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_TRANSFER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, recipient), 
                new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> transferFrom(String sender, String recipient, BigInteger amount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_TRANSFERFROM, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, sender), 
                new org.web3j.abi.datatypes.Address(160, recipient), 
                new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> transferOwnership(String newOwner) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_TRANSFEROWNERSHIP, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, newOwner)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static CLGNToken load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new CLGNToken(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static CLGNToken load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new CLGNToken(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static CLGNToken load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new CLGNToken(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static CLGNToken load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new CLGNToken(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<CLGNToken> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(CLGNToken.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    public static RemoteCall<CLGNToken> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(CLGNToken.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<CLGNToken> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(CLGNToken.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<CLGNToken> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(CLGNToken.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static class ApprovalEventResponse extends BaseEventResponse {
        public String owner;

        public String spender;

        public BigInteger value;
    }

    public static class OwnershipTransferredEventResponse extends BaseEventResponse {
        public String previousOwner;

        public String newOwner;
    }

    public static class TransferEventResponse extends BaseEventResponse {
        public String from;

        public String to;

        public BigInteger value;
    }
}
