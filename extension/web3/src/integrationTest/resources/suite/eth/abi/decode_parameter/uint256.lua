local eth = require('web3.eth')
local types = eth.abi.types
local err, result = eth.abi.decode_parameter(types.UINT_256,'0x0000000000000000000000000000000000000000000000000000000000000010')
assert(err == nil)
assert(result == 16)