arbitrum = require('web3.arbitrum').create({
    url = context.env.test_url
})

err, blocks = arbitrum.get_blocks({ 1048580, 1048579, 1048578 })
assert(err == nil)
assert(#blocks == 3)

