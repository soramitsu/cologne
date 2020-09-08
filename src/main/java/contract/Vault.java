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
import org.web3j.abi.datatypes.Bool;
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
    public static final String BINARY = "6080604052600060078190556009819055600a819055600c55600d805460ff191690553480156200002f57600080fd5b506040516200219d3803806200219d833981810160405260c08110156200005557600080fd5b50805160208201516040808401516060850151608086015160a090960151600080546001600160a01b0319166001600160a01b038089169190911780835595519798969794969395939492938993911691907f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e0908290a350600280546001600160a01b0319163317908190556040805163077701eb60e41b815290516001600160a01b0392909216916377701eb091600480820192602092909190829003018186803b1580156200012557600080fd5b505afa1580156200013a573d6000803e3d6000fd5b505050506040513d60208110156200015157600080fd5b5051600380546001600160a01b0319166001600160a01b039283161790556002546040805163642c9e8960e11b81529051919092169163c8593d12916004808301926020929190829003018186803b158015620001ad57600080fd5b505afa158015620001c2573d6000803e3d6000fd5b505050506040513d6020811015620001d957600080fd5b5051600480546001600160a01b03199081166001600160a01b039384161782556005805490911687841617905560068590556007849055600d8054610100600160a81b0319166101008585168102919091179182905560408051632abf68dd60e11b81529051919092049093169263557ed1ba92828101926020929190829003018186803b1580156200026b57600080fd5b505afa15801562000280573d6000803e3d6000fd5b505050506040513d60208110156200029757600080fd5b5051600b55620002a785620002b3565b505050505050620002be565b600880549091019055565b611ecf80620002ce6000396000f3fe608060405234801561001057600080fd5b50600436106100f55760003560e01c80638da5cb5b11610097578063c5ebeaec11610066578063c5ebeaec146101ea578063f2fde38b14610207578063f7376f0c1461022d578063f78be85a14610235576100f5565b80638da5cb5b1461019a5780638f32d59b146101be57806398d5fdca146101da578063a69bdf16146101e2576100f5565b806343d726d6116100d357806343d726d614610129578063486501c51461013157806359a87bc114610160578063715018a614610192576100f5565b80631865c57d146100fa578063243582ff146101045780632da25de314610121575b600080fd5b61010261023d565b005b6101026004803603602081101561011a57600080fd5b503561023f565b610102610541565b61010261058b565b61014e6004803603602081101561014757600080fd5b503561098b565b60408051918252519081900360200190f35b6101026004803603606081101561017657600080fd5b50803590602081013590604001356001600160a01b03166109fc565b610102610c82565b6101a2610cdd565b604080516001600160a01b039092168252519081900360200190f35b6101c6610ced565b604080519115158252519081900360200190f35b61014e610cfe565b61014e610d89565b6101026004803603602081101561020057600080fd5b5035610ddd565b6101026004803603602081101561021d57600080fd5b50356001600160a01b0316610f9f565b61014e610fb9565b61014e611123565b565b600d5460ff1615610289576040805162461bcd60e51b815260206004820152600f60248201526e15985d5b1d081a5cc818db1bdcd959608a1b604482015290519081900360640190fd5b600354604080516323b872dd60e01b81523360048201523060248201526044810184905290516001600160a01b03909216916323b872dd916064808201926020929091908290030181600087803b1580156102e357600080fd5b505af11580156102f7573d6000803e3d6000fd5b505050506040513d602081101561030d57600080fd5b5051610360576040805162461bcd60e51b815260206004820152601b60248201527f5661756c743a2063616e6e6f74207472616e73666572204541552e0000000000604482015290519081900360640190fd5b6103e5600182600d60019054906101000a90046001600160a01b03166001600160a01b031663557ed1ba6040518163ffffffff1660e01b815260040160206040518083038186803b1580156103b457600080fd5b505afa1580156103c8573d6000803e3d6000fd5b505050506040513d60208110156103de57600080fd5b505161053c565b600d60019054906101000a90046001600160a01b03166001600160a01b031663557ed1ba6040518163ffffffff1660e01b815260040160206040518083038186803b15801561043357600080fd5b505afa158015610447573d6000803e3d6000fd5b505050506040513d602081101561045d57600080fd5b5051600b55600061046d8261120a565b9050600060095482111561048f5750600980546000909155908190039061049b565b50600980548290039055805b60035460408051630852cd8d60e31b81526004810184905290516001600160a01b03909216916342966c689160248082019260009290919082900301818387803b1580156104e857600080fd5b505af11580156104fc573d6000803e3d6000fd5b50505050610522600461051c60075460065461184390919063ffffffff16565b906118a5565b600a54600954610531916118e7565b1161053c576000600c555b505050565b600d5460ff161561023d576040805162461bcd60e51b815260206004820152600f60248201526e15985d5b1d081a5cc818db1bdcd959608a1b604482015290519081900360640190fd5b610593610ced565b61059c57600080fd5b61061e600d60019054906101000a90046001600160a01b03166001600160a01b031663557ed1ba6040518163ffffffff1660e01b815260040160206040518083038186803b1580156105ed57600080fd5b505afa158015610601573d6000803e3d6000fd5b505050506040513d602081101561061757600080fd5b505161098b565b1561065a5760405162461bcd60e51b8152600401808060200182810382526037815260200180611d6c6037913960400191505060405180910390fd5b6005546001600160a01b031663a9059cbb610673610cdd565b600554604080516370a0823160e01b815230600482015290516001600160a01b03909216916370a0823191602480820192602092909190829003018186803b1580156106be57600080fd5b505afa1580156106d2573d6000803e3d6000fd5b505050506040513d60208110156106e857600080fd5b5051604080516001600160e01b031960e086901b1681526001600160a01b03909316600484015260248301919091525160448083019260209291908290030181600087803b15801561073957600080fd5b505af115801561074d573d6000803e3d6000fd5b505050506040513d602081101561076357600080fd5b50506004546001600160a01b031663a9059cbb61077e610cdd565b60048054604080516370a0823160e01b81523093810193909352516001600160a01b03909116916370a08231916024808301926020929190829003018186803b1580156107ca57600080fd5b505afa1580156107de573d6000803e3d6000fd5b505050506040513d60208110156107f457600080fd5b5051604080516001600160e01b031960e086901b1681526001600160a01b03909316600484015260248301919091525160448083019260209291908290030181600087803b15801561084557600080fd5b505af1158015610859573d6000803e3d6000fd5b505050506040513d602081101561086f57600080fd5b50506003546001600160a01b031663a9059cbb61088a610cdd565b600354604080516370a0823160e01b815230600482015290516001600160a01b03909216916370a0823191602480820192602092909190829003018186803b1580156108d557600080fd5b505afa1580156108e9573d6000803e3d6000fd5b505050506040513d60208110156108ff57600080fd5b5051604080516001600160e01b031960e086901b1681526001600160a01b03909316600484015260248301919091525160448083019260209291908290030181600087803b15801561095057600080fd5b505af1158015610964573d6000803e3d6000fd5b505050506040513d602081101561097a57600080fd5b5050600d805460ff19166001179055565b600d5460009060ff16156109d8576040805162461bcd60e51b815260206004820152600f60248201526e15985d5b1d081a5cc818db1bdcd959608a1b604482015290519081900360640190fd5b60006109e383611941565b50506009549091506109f590826118e7565b9392505050565b600d5460ff1615610a46576040805162461bcd60e51b815260206004820152600f60248201526e15985d5b1d081a5cc818db1bdcd959608a1b604482015290519081900360640190fd5b600554604080516370a0823160e01b815230600482015290516001600160a01b03909216916370a0823191602480820192602092909190829003018186803b158015610a9157600080fd5b505afa158015610aa5573d6000803e3d6000fd5b505050506040513d6020811015610abb57600080fd5b5051831115610afb5760405162461bcd60e51b8152600401808060200182810382526027815260200180611e736027913960400191505060405180910390fd5b6000610b05610cfe565b905082811115610b5c576040805162461bcd60e51b815260206004820152601b60248201527f5661756c743a3a62757928293a20507269636520746f6f206c6f770000000000604482015290519081900360640190fd5b838102610b688161023f565b6005546040805163a9059cbb60e01b81526001600160a01b038681166004830152602482018990529151919092169163a9059cbb9160448083019260209291908290030181600087803b158015610bbe57600080fd5b505af1158015610bd2573d6000803e3d6000fd5b505050506040513d6020811015610be857600080fd5b5051610c3b576040805162461bcd60e51b815260206004820181905260248201527f5661756c743a3a6275793a2063616e6e6f74207472616e73666572204541552e604482015290519081900360640190fd5b6040805186815290516001600160a01b0385169184917f9d995b79e708615dc7201d66ebdcb94d773fe76aac960305383a8b4f8dda9f059181900360200190a35050505050565b610c8a610ced565b610c9357600080fd5b600080546040516001600160a01b03909116907f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e0908390a3600080546001600160a01b0319169055565b6000546001600160a01b03165b90565b6000546001600160a01b0316331490565b6000610d82600d60019054906101000a90046001600160a01b03166001600160a01b031663557ed1ba6040518163ffffffff1660e01b815260040160206040518083038186803b158015610d5157600080fd5b505afa158015610d65573d6000803e3d6000fd5b505050506040513d6020811015610d7b57600080fd5b5051611941565b5091505090565b600d5460009060ff1615610dd6576040805162461bcd60e51b815260206004820152600f60248201526e15985d5b1d081a5cc818db1bdcd959608a1b604482015290519081900360640190fd5b5060095490565b600d5460ff1615610e27576040805162461bcd60e51b815260206004820152600f60248201526e15985d5b1d081a5cc818db1bdcd959608a1b604482015290519081900360640190fd5b610e2f610ced565b610e3857600080fd5b610e40611123565b811115610e94576040805162461bcd60e51b815260206004820152601a60248201527f437265646974206c696d69742069732065786861757374656420000000000000604482015290519081900360640190fd5b6002546001600160a01b031663f97c6e11610ead610cdd565b836040518363ffffffff1660e01b815260040180836001600160a01b0316815260200182815260200192505050600060405180830381600087803b158015610ef457600080fd5b505af1158015610f08573d6000803e3d6000fd5b505060098054840190555050600d5460408051632abf68dd60e11b815290516101009092046001600160a01b03169163557ed1ba91600480820192602092909190829003018186803b158015610f5d57600080fd5b505afa158015610f71573d6000803e3d6000fd5b505050506040513d6020811015610f8757600080fd5b5051600b819055610f9c90600290839061053c565b50565b610fa7610ced565b610fb057600080fd5b610f9c81611a50565b6002546040805163634c50ed60e11b815290516000926001600160a01b03169163c698a1da916004808301926020929190829003018186803b158015610ffe57600080fd5b505afa158015611012573d6000803e3d6000fd5b505050506040513d602081101561102857600080fd5b50516002546040805163642c9e8960e11b815290516001600160a01b0393841693633ddac95393169163c8593d12916004808301926020929190829003018186803b15801561107657600080fd5b505afa15801561108a573d6000803e3d6000fd5b505050506040513d60208110156110a057600080fd5b5051600854604080516001600160e01b031960e086901b1681526001600160a01b0390931660048401526024830191909152516044808301926020929190829003018186803b1580156110f257600080fd5b505afa158015611106573d6000803e3d6000fd5b505050506040513d602081101561111c57600080fd5b5051905090565b600d5460009060ff1615611170576040805162461bcd60e51b815260206004820152600f60248201526e15985d5b1d081a5cc818db1bdcd959608a1b604482015290519081900360640190fd5b60006111c3600d60019054906101000a90046001600160a01b03166001600160a01b031663557ed1ba6040518163ffffffff1660e01b815260040160206040518083038186803b1580156105ed57600080fd5b905060006111e3600461051c60075460065461184390919063ffffffff16565b90508181116111f757600092505050610cea565b6112018183611abe565b92505050610cea565b600080600080611261600d60019054906101000a90046001600160a01b03166001600160a01b031663557ed1ba6040518163ffffffff1660e01b815260040160206040518083038186803b158015610d5157600080fd5b6007829055600c8190558796509194509250905060008385111561129057506000600a5592829003928261129b565b50838303600a556000935b60006112a88260026118a5565b905060006113b6600a61051c6009600260009054906101000a90046001600160a01b03166001600160a01b031663c698a1da6040518163ffffffff1660e01b815260040160206040518083038186803b15801561130457600080fd5b505afa158015611318573d6000803e3d6000fd5b505050506040513d602081101561132e57600080fd5b505160035460408051633ddac95360e01b81526001600160a01b039283166004820152602481018a905290519190921691633ddac953916044808301926020929190829003018186803b15801561138457600080fd5b505afa158015611398573d6000803e3d6000fd5b505050506040513d60208110156113ae57600080fd5b505190611843565b604080516002808252606080830184529394509091602083019080368337505060035482519293506001600160a01b0316918391506000906113f457fe5b6001600160a01b03928316602091820292909201015260045482519116908290600190811061141f57fe5b60200260200101906001600160a01b031690816001600160a01b0316815250506000600d60019054906101000a90046001600160a01b03166001600160a01b031663557ed1ba6040518163ffffffff1660e01b815260040160206040518083038186803b15801561148f57600080fd5b505afa1580156114a3573d6000803e3d6000fd5b505050506040513d60208110156114b957600080fd5b505160025460408051633349af8760e21b8152905161271090930193506060926001600160a01b039092169163cd26be1c91600480820192602092909190829003018186803b15801561150b57600080fd5b505afa15801561151f573d6000803e3d6000fd5b505050506040513d602081101561153557600080fd5b50516040516338ed173960e01b8152600481018781526024820187905230606483018190526084830186905260a060448401908152875160a485015287516001600160a01b03909516946338ed1739948b948b948b9490938b9360c401906020878101910280838360005b838110156115b85781810151838201526020016115a0565b505050509050019650505050505050600060405180830381600087803b1580156115e157600080fd5b505af11580156115f5573d6000803e3d6000fd5b505050506040513d6000823e601f3d908101601f19168201604052602081101561161e57600080fd5b810190808051604051939291908464010000000082111561163e57600080fd5b90830190602082018581111561165357600080fd5b825186602082028301116401000000008211171561167057600080fd5b82525081516020918201928201910280838360005b8381101561169d578181015183820152602001611685565b5050505090500160405250505090506000816001815181106116bb57fe5b60200260200101519050816000815181106116d257fe5b602002602001015186146117175760405162461bcd60e51b8152600401808060200182810382526039815260200180611e3a6039913960400191505060405180910390fd5b808510156117565760405162461bcd60e51b8152600401808060200182810382526032815260200180611e086032913960400191505060405180910390fd5b6004805460408051630852cd8d60e31b8152928301849052516001600160a01b03909116916342966c6891602480830192600092919082900301818387803b1580156117a157600080fd5b505af11580156117b5573d6000803e3d6000fd5b5050600354604080516391c05b0b60e01b81528a8c03600482015290516001600160a01b0390921693506391c05b0b92506024808201926020929091908290030181600087803b15801561180857600080fd5b505af115801561181c573d6000803e3d6000fd5b505050506040513d602081101561183257600080fd5b505050505050505050505050919050565b6000826118525750600061189f565b8282028284828161185f57fe5b041461189c5760405162461bcd60e51b8152600401808060200182810382526021815260200180611da36021913960400191505060405180910390fd5b90505b92915050565b600061189c83836040518060400160405280601a81526020017f536166654d6174683a206469766973696f6e206279207a65726f000000000000815250611b00565b60008282018381101561189c576040805162461bcd60e51b815260206004820152601b60248201527f536166654d6174683a206164646974696f6e206f766572666c6f770000000000604482015290519081900360640190fd5b6000806000600b5484101561199d576040805162461bcd60e51b815260206004820181905260248201527f43616e6e6f742063616c63756c6174652066656520696e207468652070617374604482015290519081900360640190fd5b6201518060006119b2620186a061016d6118a5565b9050600c5492506119c283611ba2565b600a54600b549096509094505b86811015611a465760006119f3600461051c8860065461184390919063ffffffff16565b905080611a0b886009546118e790919063ffffffff16565b1115611a265784611a1a578194505b611a2385611ba2565b95505b620f42408388600954010281611a3857fe5b0496909601955082016119cf565b5050509193909250565b6001600160a01b038116611a6357600080fd5b600080546040516001600160a01b03808516939216917f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e091a3600080546001600160a01b0319166001600160a01b0392909216919091179055565b600061189c83836040518060400160405280601e81526020017f536166654d6174683a207375627472616374696f6e206f766572666c6f770000815250611d11565b60008183611b8c5760405162461bcd60e51b81526004018080602001828103825283818151815260200191508051906020019080838360005b83811015611b51578181015183820152602001611b39565b50505050905090810190601f168015611b7e5780820380516001836020036101000a031916815260200191505b509250505060405180910390fd5b506000838581611b9857fe5b0495945050505050565b6007546107088215611d0b5782600d60019054906101000a90046001600160a01b03166001600160a01b031663557ed1ba6040518163ffffffff1660e01b815260040160206040518083038186803b158015611bfd57600080fd5b505afa158015611c11573d6000803e3d6000fd5b505050506040513d6020811015611c2757600080fd5b50511015611c665760405162461bcd60e51b8152600401808060200182810382526044815260200180611dc46044913960600191505060405180910390fd5b6000611cf08261051c86600d60019054906101000a90046001600160a01b03166001600160a01b031663557ed1ba6040518163ffffffff1660e01b815260040160206040518083038186803b158015611cbe57600080fd5b505afa158015611cd2573d6000803e3d6000fd5b505050506040513d6020811015611ce857600080fd5b505190611abe565b606590069050611d07606461051c85848303611843565b9250505b50919050565b60008184841115611d635760405162461bcd60e51b8152602060048201818152835160248401528351909283926044909101919085019080838360008315611b51578181015183820152602001611b39565b50505090039056fe5661756c743a3a636c6f736528293a20636c6f736520616c6c6f776564206f6e6c792069662064656274206973207061796564206f6666536166654d6174683a206d756c7469706c69636174696f6e206f766572666c6f775661756c743a3a676574507269636528293a20496e636f72726563742073746174653a204c696d697420697320627265616368656420696e2074686520667574757265215661756c743a3a7061794f666628293a204d444c5920626f75676874206973206c657373207468616e2065787065637465645661756c743a3a7061794f666628293a206e6f7420657861637420616d6f756e74206f662045415520736f6c6420746f20627579204d444c595661756c743a3a62757928293a204e6f7420656e6f75676820746f6b656e7320746f2073656c6ca264697066735822122009012efa49e73366c82d9e6b1a9e9552161db3f28d00ca2761e2518ea887cb8664736f6c63430007010033";

    public static final String FUNC_BORROW = "borrow";

    public static final String FUNC_BUY = "buy";

    public static final String FUNC_CLOSE = "close";

    public static final String FUNC_GETCOLLATERALINEAU = "getCollateralInEau";

    public static final String FUNC_GETCREDITLIMIT = "getCreditLimit";

    public static final String FUNC_GETPRICE = "getPrice";

    public static final String FUNC_GETPRINCIPAL = "getPrincipal";

    public static final String FUNC_GETSTATE = "getState";

    public static final String FUNC_GETTOTALDEBT = "getTotalDebt";

    public static final String FUNC_ISOWNER = "isOwner";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_PAYOFF = "payOff";

    public static final String FUNC_RENOUNCEOWNERSHIP = "renounceOwnership";

    public static final String FUNC_SLASH = "slash";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final Event OWNERSHIPTRANSFERRED_EVENT = new Event("OwnershipTransferred", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    ;

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

    public RemoteFunctionCall<Boolean> isOwner() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_ISOWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<String> owner() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_OWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> payOff(BigInteger amount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_PAYOFF, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> renounceOwnership() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_RENOUNCEOWNERSHIP, 
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

    public RemoteFunctionCall<TransactionReceipt> transferOwnership(String newOwner) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_TRANSFEROWNERSHIP, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, newOwner)), 
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

    public static RemoteCall<Vault> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider, String owner, BigInteger stake, String token, BigInteger initialAmount, BigInteger tokenPrice, String timeProvider) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, owner), 
                new org.web3j.abi.datatypes.generated.Uint256(stake), 
                new org.web3j.abi.datatypes.Address(160, token), 
                new org.web3j.abi.datatypes.generated.Uint256(initialAmount), 
                new org.web3j.abi.datatypes.generated.Uint256(tokenPrice), 
                new org.web3j.abi.datatypes.Address(160, timeProvider)));
        return deployRemoteCall(Vault.class, web3j, credentials, contractGasProvider, BINARY, encodedConstructor);
    }

    public static RemoteCall<Vault> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider, String owner, BigInteger stake, String token, BigInteger initialAmount, BigInteger tokenPrice, String timeProvider) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, owner), 
                new org.web3j.abi.datatypes.generated.Uint256(stake), 
                new org.web3j.abi.datatypes.Address(160, token), 
                new org.web3j.abi.datatypes.generated.Uint256(initialAmount), 
                new org.web3j.abi.datatypes.generated.Uint256(tokenPrice), 
                new org.web3j.abi.datatypes.Address(160, timeProvider)));
        return deployRemoteCall(Vault.class, web3j, transactionManager, contractGasProvider, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<Vault> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String owner, BigInteger stake, String token, BigInteger initialAmount, BigInteger tokenPrice, String timeProvider) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, owner), 
                new org.web3j.abi.datatypes.generated.Uint256(stake), 
                new org.web3j.abi.datatypes.Address(160, token), 
                new org.web3j.abi.datatypes.generated.Uint256(initialAmount), 
                new org.web3j.abi.datatypes.generated.Uint256(tokenPrice), 
                new org.web3j.abi.datatypes.Address(160, timeProvider)));
        return deployRemoteCall(Vault.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<Vault> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String owner, BigInteger stake, String token, BigInteger initialAmount, BigInteger tokenPrice, String timeProvider) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, owner), 
                new org.web3j.abi.datatypes.generated.Uint256(stake), 
                new org.web3j.abi.datatypes.Address(160, token), 
                new org.web3j.abi.datatypes.generated.Uint256(initialAmount), 
                new org.web3j.abi.datatypes.generated.Uint256(tokenPrice), 
                new org.web3j.abi.datatypes.Address(160, timeProvider)));
        return deployRemoteCall(Vault.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static class OwnershipTransferredEventResponse extends BaseEventResponse {
        public String previousOwner;

        public String newOwner;
    }

    public static class PurchaseEventResponse extends BaseEventResponse {
        public BigInteger price;

        public String to;

        public BigInteger amount;
    }
}
