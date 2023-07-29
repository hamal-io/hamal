local abi = web3.eth.abi

local err, result = abi.decode_parameter('uint256', '0x0000000000000000000000000000000000000000000000000000000000000010')
assert(err == nil)
assert(result == 16)
