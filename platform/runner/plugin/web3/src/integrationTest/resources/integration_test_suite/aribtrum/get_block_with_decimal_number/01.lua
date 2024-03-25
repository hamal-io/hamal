evm = require_plugin("web3.evm")

res = fail_on_error(evm.execute({
    url = context.env.test_url .. '/arbitrum',
    requests = { evm.get_block(1048578) }
}))

block = res[1].result
assert(block.number == '0x100002')
assert(block.hash == '0xbe1ba35d8f2b0911fe5874bc5fcaab85b75cf121468872935f6ec7b62075c7b6')
