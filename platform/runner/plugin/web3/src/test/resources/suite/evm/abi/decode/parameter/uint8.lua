local eth = require_plugin('web3.evm')
local types = eth.abi.types
local err, result = eth.abi.decode_parameter(types.UINT_8,'0x000000000000000000000000000000000000000000000000000000000000007f')
assert(err == nil)
assert(result == 127)