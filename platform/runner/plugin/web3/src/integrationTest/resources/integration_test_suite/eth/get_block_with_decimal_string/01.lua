evm = require_plugin("web3.evm")

res = fail_on_error(evm.execute({
    url = context.env.test_url,
    requests = { evm.get_block('1048578') }
}))

block = res[1].result
assert(block.number == '0x100002')
assert(block.hash == '0x54dd95b568a08a6bcc3ad1675810b8ec841ef5eed7ac027cce35bb2a097671f9')
