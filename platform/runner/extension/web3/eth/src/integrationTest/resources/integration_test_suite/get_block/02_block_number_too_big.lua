eth = require('web3.eth').create({
    url = context.env.test_url
})

err, block = eth.get_block('0xfffffffffffffffffff')
assert(err ~= nil)
assert(err.message == 'Value must be <= 18446744073709551615')

assert(block == nil)


