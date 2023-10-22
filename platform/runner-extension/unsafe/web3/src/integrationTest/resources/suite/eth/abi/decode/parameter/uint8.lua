local eth = require('web3.eth')
local types = eth.abi.types
local err, result = eth.abi.decode_parameter(types.UINT_8,'0x000000000000000000000000000000000000000000000000000000000000007f')
assert(err == nil)
assert(result == 127)