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
    public static final String BINARY = "6080604052600060078190556009819055600a819055600c819055600e55600f805460ff60a01b191690553480156200003757600080fd5b506040516200248c3803806200248c833981810160405260c08110156200005d57600080fd5b50805160208201516040808401516060850151608086015160a090960151600080546001600160a01b0319166001600160a01b038089169190911780835595519798969794969395939492938993911691907f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e0908290a350600280546001600160a01b0319163317908190556040805163077701eb60e41b815290516001600160a01b0392909216916377701eb091600480820192602092909190829003018186803b1580156200012d57600080fd5b505afa15801562000142573d6000803e3d6000fd5b505050506040513d60208110156200015957600080fd5b5051600380546001600160a01b0319166001600160a01b039283161790556002546040805163642c9e8960e11b81529051919092169163c8593d12916004808301926020929190829003018186803b158015620001b557600080fd5b505afa158015620001ca573d6000803e3d6000fd5b505050506040513d6020811015620001e157600080fd5b5051600480546001600160a01b03199081166001600160a01b0393841617825560058054821688851617905560068690556007859055600d8054909116848416179081905560408051632abf68dd60e11b81529051919093169263557ed1ba9281810192602092909190829003018186803b1580156200026057600080fd5b505afa15801562000275573d6000803e3d6000fd5b505050506040513d60208110156200028c57600080fd5b5051600b556200029c85620002a8565b505050505050620002b3565b600880549091019055565b6121c980620002c36000396000f3fe608060405234801561001057600080fd5b506004361061010b5760003560e01c80638da5cb5b116100a2578063a69bdf1611610071578063a69bdf1614610208578063c5ebeaec14610210578063f2fde38b1461022d578063f7376f0c14610253578063f78be85a1461025b5761010b565b80638da5cb5b146101cc5780638f32d59b146101f057806398d5fdca146101f85780639b434cf1146102005761010b565b806343d726d6116100de57806343d726d61461015b578063486501c51461016357806359a87bc114610192578063715018a6146101c45761010b565b80631865c57d1461011057806321adeed61461011a578063243582ff146101365780632da25de314610153575b600080fd5b610118610263565b005b610122610265565b604080519115158252519081900360200190f35b6101186004803603602081101561014c57600080fd5b5035610349565b610118610652565b6101186106a3565b6101806004803603602081101561017957600080fd5b5035610a6d565b60408051918252519081900360200190f35b610118600480360360608110156101a857600080fd5b50803590602081013590604001356001600160a01b0316610ae4565b610118610dbf565b6101d4610e1a565b604080516001600160a01b039092168252519081900360200190f35b610122610e29565b610180610e3a565b610118610e44565b610180610fa8565b6101186004803603602081101561022657600080fd5b5035611003565b6101186004803603602081101561024357600080fd5b50356001600160a01b03166111c8565b6101806111e2565b61018061134c565b565b600f54600090600160a01b900460ff16156102b9576040805162461bcd60e51b815260206004820152600f60248201526e15985d5b1d081a5cc818db1bdcd959608a1b604482015290519081900360640190fd5b600d5460408051632abf68dd60e11b81529051610330926001600160a01b03169163557ed1ba916004808301926020929190829003018186803b1580156102ff57600080fd5b505afa158015610313573d6000803e3d6000fd5b505050506040513d602081101561032957600080fd5b5051610a6d565b15801590610343575061034161134c565b155b90505b90565b600f54600160a01b900460ff161561039a576040805162461bcd60e51b815260206004820152600f60248201526e15985d5b1d081a5cc818db1bdcd959608a1b604482015290519081900360640190fd5b600354604080516323b872dd60e01b81523360048201523060248201526044810184905290516001600160a01b03909216916323b872dd916064808201926020929091908290030181600087803b1580156103f457600080fd5b505af1158015610408573d6000803e3d6000fd5b505050506040513d602081101561041e57600080fd5b5051610471576040805162461bcd60e51b815260206004820152601b60248201527f5661756c743a2063616e6e6f74207472616e73666572204541552e0000000000604482015290519081900360640190fd5b6104f6600182600d60009054906101000a90046001600160a01b03166001600160a01b031663557ed1ba6040518163ffffffff1660e01b815260040160206040518083038186803b1580156104c557600080fd5b505afa1580156104d9573d6000803e3d6000fd5b505050506040513d60208110156104ef57600080fd5b505161064d565b600d60009054906101000a90046001600160a01b03166001600160a01b031663557ed1ba6040518163ffffffff1660e01b815260040160206040518083038186803b15801561054457600080fd5b505afa158015610558573d6000803e3d6000fd5b505050506040513d602081101561056e57600080fd5b5051600b55600061057e8261143a565b905060006009548211156105a0575060098054600090915590819003906105ac565b50600980548290039055805b60035460408051630852cd8d60e31b81526004810184905290516001600160a01b03909216916342966c689160248082019260009290919082900301818387803b1580156105f957600080fd5b505af115801561060d573d6000803e3d6000fd5b50505050610633600461062d600754600654611a8090919063ffffffff16565b90611ae2565b600a5460095461064291611b24565b1161064d576000600c555b505050565b600f54600160a01b900460ff1615610263576040805162461bcd60e51b815260206004820152600f60248201526e15985d5b1d081a5cc818db1bdcd959608a1b604482015290519081900360640190fd5b6106ab610e29565b6106b457600080fd5b600d5460408051632abf68dd60e11b815290516106fa926001600160a01b03169163557ed1ba916004808301926020929190829003018186803b1580156102ff57600080fd5b156107365760405162461bcd60e51b81526004018080602001828103825260368152602001806120496036913960400191505060405180910390fd5b6005546001600160a01b031663a9059cbb61074f610e1a565b600554604080516370a0823160e01b815230600482015290516001600160a01b03909216916370a0823191602480820192602092909190829003018186803b15801561079a57600080fd5b505afa1580156107ae573d6000803e3d6000fd5b505050506040513d60208110156107c457600080fd5b5051604080516001600160e01b031960e086901b1681526001600160a01b03909316600484015260248301919091525160448083019260209291908290030181600087803b15801561081557600080fd5b505af1158015610829573d6000803e3d6000fd5b505050506040513d602081101561083f57600080fd5b50506004546001600160a01b031663a9059cbb61085a610e1a565b60048054604080516370a0823160e01b81523093810193909352516001600160a01b03909116916370a08231916024808301926020929190829003018186803b1580156108a657600080fd5b505afa1580156108ba573d6000803e3d6000fd5b505050506040513d60208110156108d057600080fd5b5051604080516001600160e01b031960e086901b1681526001600160a01b03909316600484015260248301919091525160448083019260209291908290030181600087803b15801561092157600080fd5b505af1158015610935573d6000803e3d6000fd5b505050506040513d602081101561094b57600080fd5b50506003546001600160a01b031663a9059cbb610966610e1a565b600354604080516370a0823160e01b815230600482015290516001600160a01b03909216916370a0823191602480820192602092909190829003018186803b1580156109b157600080fd5b505afa1580156109c5573d6000803e3d6000fd5b505050506040513d60208110156109db57600080fd5b5051604080516001600160e01b031960e086901b1681526001600160a01b03909316600484015260248301919091525160448083019260209291908290030181600087803b158015610a2c57600080fd5b505af1158015610a40573d6000803e3d6000fd5b505050506040513d6020811015610a5657600080fd5b5050600f805460ff60a01b1916600160a01b179055565b600f54600090600160a01b900460ff1615610ac1576040805162461bcd60e51b815260206004820152600f60248201526e15985d5b1d081a5cc818db1bdcd959608a1b604482015290519081900360640190fd5b6000610acc83611b7e565b50600954909150610add9082611b24565b9392505050565b600f54600160a01b900460ff1615610b35576040805162461bcd60e51b815260206004820152600f60248201526e15985d5b1d081a5cc818db1bdcd959608a1b604482015290519081900360640190fd5b600554604080516370a0823160e01b815230600482015290516001600160a01b03909216916370a0823191602480820192602092909190829003018186803b158015610b8057600080fd5b505afa158015610b94573d6000803e3d6000fd5b505050506040513d6020811015610baa57600080fd5b5051831115610bea5760405162461bcd60e51b815260040180806020018281038252602781526020018061216d6027913960400191505060405180910390fd5b6000610bf4610e3a565b905060008111610c355760405162461bcd60e51b815260040180806020018281038252602f815260200180611ff9602f913960400191505060405180910390fd5b82811115610c8a576040805162461bcd60e51b815260206004820152601b60248201527f5661756c743a3a62757928293a20507269636520746f6f206c6f770000000000604482015290519081900360640190fd5b838102610c9681610349565b6005546040805163a9059cbb60e01b81526001600160a01b038681166004830152602482018990529151919092169163a9059cbb9160448083019260209291908290030181600087803b158015610cec57600080fd5b505af1158015610d00573d6000803e3d6000fd5b505050506040513d6020811015610d1657600080fd5b5051610d69576040805162461bcd60e51b815260206004820181905260248201527f5661756c743a3a6275793a2063616e6e6f74207472616e73666572204541552e604482015290519081900360640190fd5b8160075414610d785760078290555b6040805186815290516001600160a01b0385169184917f9d995b79e708615dc7201d66ebdcb94d773fe76aac960305383a8b4f8dda9f059181900360200190a35050505050565b610dc7610e29565b610dd057600080fd5b600080546040516001600160a01b03909116907f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e0908390a3600080546001600160a01b0319169055565b6000546001600160a01b031690565b6000546001600160a01b0316331490565b6000610343611c95565b600f54600160a01b900460ff1615610e95576040805162461bcd60e51b815260206004820152600f60248201526e15985d5b1d081a5cc818db1bdcd959608a1b604482015290519081900360640190fd5b610e9d610265565b610ed85760405162461bcd60e51b8152600401808060200182810382526043815260200180611fb66043913960600191505060405180910390fd5b600e5415610f175760405162461bcd60e51b815260040180806020018281038252603f81526020018061212e603f913960400191505060405180910390fd5b600d60009054906101000a90046001600160a01b03166001600160a01b031663557ed1ba6040518163ffffffff1660e01b815260040160206040518083038186803b158015610f6557600080fd5b505afa158015610f79573d6000803e3d6000fd5b505050506040513d6020811015610f8f57600080fd5b5051600e55600f80546001600160a01b03191633179055565b600f54600090600160a01b900460ff1615610ffc576040805162461bcd60e51b815260206004820152600f60248201526e15985d5b1d081a5cc818db1bdcd959608a1b604482015290519081900360640190fd5b5060095490565b600f54600160a01b900460ff1615611054576040805162461bcd60e51b815260206004820152600f60248201526e15985d5b1d081a5cc818db1bdcd959608a1b604482015290519081900360640190fd5b61105c610e29565b61106557600080fd5b61106d61134c565b8111156110c1576040805162461bcd60e51b815260206004820152601a60248201527f437265646974206c696d69742069732065786861757374656420000000000000604482015290519081900360640190fd5b6002546001600160a01b031663f97c6e116110da610e1a565b836040518363ffffffff1660e01b815260040180836001600160a01b0316815260200182815260200192505050600060405180830381600087803b15801561112157600080fd5b505af1158015611135573d6000803e3d6000fd5b505060098054840190555050600d5460408051632abf68dd60e11b815290516001600160a01b039092169163557ed1ba91600480820192602092909190829003018186803b15801561118657600080fd5b505afa15801561119a573d6000803e3d6000fd5b505050506040513d60208110156111b057600080fd5b5051600b8190556111c590600290839061064d565b50565b6111d0610e29565b6111d957600080fd5b6111c581611e09565b6002546040805163634c50ed60e11b815290516000926001600160a01b03169163c698a1da916004808301926020929190829003018186803b15801561122757600080fd5b505afa15801561123b573d6000803e3d6000fd5b505050506040513d602081101561125157600080fd5b50516002546040805163642c9e8960e11b815290516001600160a01b0393841693633ddac95393169163c8593d12916004808301926020929190829003018186803b15801561129f57600080fd5b505afa1580156112b3573d6000803e3d6000fd5b505050506040513d60208110156112c957600080fd5b5051600854604080516001600160e01b031960e086901b1681526001600160a01b0390931660048401526024830191909152516044808301926020929190829003018186803b15801561131b57600080fd5b505afa15801561132f573d6000803e3d6000fd5b505050506040513d602081101561134557600080fd5b5051905090565b600f54600090600160a01b900460ff16156113a0576040805162461bcd60e51b815260206004820152600f60248201526e15985d5b1d081a5cc818db1bdcd959608a1b604482015290519081900360640190fd5b60006113f3600d60009054906101000a90046001600160a01b03166001600160a01b031663557ed1ba6040518163ffffffff1660e01b815260040160206040518083038186803b1580156102ff57600080fd5b90506000611413600461062d600754600654611a8090919063ffffffff16565b905081811161142757600092505050610346565b6114318183611e77565b92505050610346565b60008060006114c1600d60009054906101000a90046001600160a01b03166001600160a01b031663557ed1ba6040518163ffffffff1660e01b815260040160206040518083038186803b15801561149057600080fd5b505afa1580156114a4573d6000803e3d6000fd5b505050506040513d60208110156114ba57600080fd5b5051611b7e565b600c81905585945090925090506000828411156114e957506000600a559181900391816114f4565b50828203600a556000925b6000611501826002611ae2565b9050600061160f600a61062d6009600260009054906101000a90046001600160a01b03166001600160a01b031663c698a1da6040518163ffffffff1660e01b815260040160206040518083038186803b15801561155d57600080fd5b505afa158015611571573d6000803e3d6000fd5b505050506040513d602081101561158757600080fd5b505160035460408051633ddac95360e01b81526001600160a01b039283166004820152602481018a905290519190921691633ddac953916044808301926020929190829003018186803b1580156115dd57600080fd5b505afa1580156115f1573d6000803e3d6000fd5b505050506040513d602081101561160757600080fd5b505190611a80565b604080516002808252606080830184529394509091602083019080368337505060035482519293506001600160a01b03169183915060009061164d57fe5b6001600160a01b03928316602091820292909201015260045482519116908290600190811061167857fe5b6001600160a01b03928316602091820292909201810191909152600d5460408051632abf68dd60e11b81529051600094929092169263557ed1ba92600480840193829003018186803b1580156116cd57600080fd5b505afa1580156116e1573d6000803e3d6000fd5b505050506040513d60208110156116f757600080fd5b505160025460408051633349af8760e21b8152905161271090930193506060926001600160a01b039092169163cd26be1c91600480820192602092909190829003018186803b15801561174957600080fd5b505afa15801561175d573d6000803e3d6000fd5b505050506040513d602081101561177357600080fd5b50516040516338ed173960e01b8152600481018781526024820187905230606483018190526084830186905260a060448401908152875160a485015287516001600160a01b03909516946338ed1739948b948b948b9490938b9360c401906020878101910280838360005b838110156117f65781810151838201526020016117de565b505050509050019650505050505050600060405180830381600087803b15801561181f57600080fd5b505af1158015611833573d6000803e3d6000fd5b505050506040513d6000823e601f3d908101601f19168201604052602081101561185c57600080fd5b810190808051604051939291908464010000000082111561187c57600080fd5b90830190602082018581111561189157600080fd5b82518660208202830111640100000000821117156118ae57600080fd5b82525081516020918201928201910280838360005b838110156118db5781810151838201526020016118c3565b5050505090500160405250505090506000816001815181106118f957fe5b602002602001015190508160008151811061191057fe5b602002602001015186146119555760405162461bcd60e51b81526004018080602001828103825260398152602001806120f56039913960400191505060405180910390fd5b808510156119945760405162461bcd60e51b81526004018080602001828103825260328152602001806120c36032913960400191505060405180910390fd5b6004805460408051630852cd8d60e31b8152928301849052516001600160a01b03909116916342966c6891602480830192600092919082900301818387803b1580156119df57600080fd5b505af11580156119f3573d6000803e3d6000fd5b5050600354604080516391c05b0b60e01b81528a8c03600482015290516001600160a01b0390921693506391c05b0b92506024808201926020929091908290030181600087803b158015611a4657600080fd5b505af1158015611a5a573d6000803e3d6000fd5b505050506040513d6020811015611a7057600080fd5b5050505050505050505050919050565b600082611a8f57506000611adc565b82820282848281611a9c57fe5b0414611ad95760405162461bcd60e51b81526004018080602001828103825260218152602001806120286021913960400191505060405180910390fd5b90505b92915050565b6000611ad983836040518060400160405280601a81526020017f536166654d6174683a206469766973696f6e206279207a65726f000000000000815250611eb9565b600082820183811015611ad9576040805162461bcd60e51b815260206004820152601b60248201527f536166654d6174683a206164646974696f6e206f766572666c6f770000000000604482015290519081900360640190fd5b600080600b54831015611bd8576040805162461bcd60e51b815260206004820181905260248201527f43616e6e6f742063616c63756c6174652066656520696e207468652070617374604482015290519081900360640190fd5b600e54839015801590611bec575083600e54105b15611bf65750600e545b620151806000611c0b620186a061016d611ae2565b9050600c549350600a5494506000600b5490505b83811015611c8c576000611c45600461062d600754600654611a8090919063ffffffff16565b905080611c5d88600954611b2490919063ffffffff16565b1115611c6c5785611c6c578195505b620f42408388600954010281611c7e57fe5b049690960195508201611c1f565b50505050915091565b600754600e546107089015611e0557600e54600d60009054906101000a90046001600160a01b03166001600160a01b031663557ed1ba6040518163ffffffff1660e01b815260040160206040518083038186803b158015611cf557600080fd5b505afa158015611d09573d6000803e3d6000fd5b505050506040513d6020811015611d1f57600080fd5b50511015611d5e5760405162461bcd60e51b815260040180806020018281038252604481526020018061207f6044913960600191505060405180910390fd5b6000611dea8261062d600e54600d60009054906101000a90046001600160a01b03166001600160a01b031663557ed1ba6040518163ffffffff1660e01b815260040160206040518083038186803b158015611db857600080fd5b505afa158015611dcc573d6000803e3d6000fd5b505050506040513d6020811015611de257600080fd5b505190611e77565b606590069050611e01606461062d85848303611a80565b9250505b5090565b6001600160a01b038116611e1c57600080fd5b600080546040516001600160a01b03808516939216917f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e091a3600080546001600160a01b0319166001600160a01b0392909216919091179055565b6000611ad983836040518060400160405280601e81526020017f536166654d6174683a207375627472616374696f6e206f766572666c6f770000815250611f5b565b60008183611f455760405162461bcd60e51b81526004018080602001828103825283818151815260200191508051906020019080838360005b83811015611f0a578181015183820152602001611ef2565b50505050905090810190601f168015611f375780820380516001836020036101000a031916815260200191505b509250505060405180910390fd5b506000838581611f5157fe5b0495945050505050565b60008184841115611fad5760405162461bcd60e51b8152602060048201818152835160248401528351909283926044909101919085019080838360008315611f0a578181015183820152602001611ef2565b50505090039056fe5661756c743a3a7374617274496e697469616c4c697175696469747941756374696f6e28293a20637265646974206c696d6974206973206e6f742062726561636865645661756c743a3a62757928293a20496e697469616c204c69717569646974792041756374696f6e206973206f766572536166654d6174683a206d756c7469706c69636174696f6e206f766572666c6f775661756c743a3a636c6f736528293a20636c6f736520616c6c6f776564206f6e6c7920696620646562742069732070616964206f66665661756c743a3a676574507269636528293a20496e636f72726563742073746174653a204c696d697420697320627265616368656420696e2074686520667574757265215661756c743a3a7061794f666628293a204d444c5920626f75676874206973206c657373207468616e2065787065637465645661756c743a3a7061794f666628293a206e6f7420657861637420616d6f756e74206f662045415520736f6c6420746f20627579204d444c595661756c743a3a7374617274496e697469616c4c697175696469747941756374696f6e28293a20636c6f73652d6f757420616c72656164792063616c6c65645661756c743a3a62757928293a204e6f7420656e6f75676820746f6b656e7320746f2073656c6ca2646970667358221220e4a0a168996a3976596a6f0911466766d2b104d111a732ec4a8541a4c5cac3e264736f6c63430007000033";

    public static final String FUNC_BORROW = "borrow";

    public static final String FUNC_BUY = "buy";

    public static final String FUNC_CLOSE = "close";

    public static final String FUNC_GETCOLLATERALINEAU = "getCollateralInEau";

    public static final String FUNC_GETCREDITLIMIT = "getCreditLimit";

    public static final String FUNC_GETPRICE = "getPrice";

    public static final String FUNC_GETPRINCIPAL = "getPrincipal";

    public static final String FUNC_GETSTATE = "getState";

    public static final String FUNC_GETTOTALDEBT = "getTotalDebt";

    public static final String FUNC_ISLIMITBREACHED = "isLimitBreached";

    public static final String FUNC_ISOWNER = "isOwner";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_PAYOFF = "payOff";

    public static final String FUNC_RENOUNCEOWNERSHIP = "renounceOwnership";

    public static final String FUNC_SLASH = "slash";

    public static final String FUNC_STARTINITIALLIQUIDITYAUCTION = "startInitialLiquidityAuction";

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

    public RemoteFunctionCall<Boolean> isLimitBreached() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_ISLIMITBREACHED, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
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

    public RemoteFunctionCall<TransactionReceipt> startInitialLiquidityAuction() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_STARTINITIALLIQUIDITYAUCTION, 
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
