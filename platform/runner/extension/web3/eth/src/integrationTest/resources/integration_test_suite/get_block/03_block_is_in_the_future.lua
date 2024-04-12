eth = require('web3.eth').create({
    url = context.env.test_url
})

err, block = eth.get_block('0xfffff')
assert(err == nil)
assert(block == nil)

