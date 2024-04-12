arbitrum = require('web3.arbitrum').create({
    url = context.env.test_url
})

err, block = arbitrum.get_block('0xfffff')
assert(err == nil)
assert(block == nil)

