arbitrum = require('web3.arbitrum').create({
    url = context.env.test_url
})

err, blocks = arbitrum.get_blocks({  })
assert(err == nil)
assert(#blocks == 0)