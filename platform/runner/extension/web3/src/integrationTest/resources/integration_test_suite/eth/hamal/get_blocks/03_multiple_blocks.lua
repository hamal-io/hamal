eth = require('web3.eth').create({
    url = context.env.test_url
})

err, blocks = eth.get_blocks({ 1048580, 1048579, 1048578 })
assert(err == nil)
assert(#blocks == 3)

