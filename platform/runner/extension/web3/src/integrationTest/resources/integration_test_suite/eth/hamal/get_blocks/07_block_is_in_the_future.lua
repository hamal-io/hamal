eth = require('web3.eth').create({
    url = context.env.test_url
})

err, blocks = eth.get_blocks({ '0xfffff' })
assert(err == nil)
assert(#blocks == 0)

