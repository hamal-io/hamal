eth = require('web3.eth').create({
    url = context.env.test_url
})

err, block = eth.get_block(0x100003)
assert(err == nil)
assert(block.number == '0x100003')

err, block = eth.get_block(1048579)
assert(err == nil)
assert(block.number == '0x100003')

err, block = eth.get_block('0x100003')
assert(err == nil)
assert(block.number == '0x100003')

err, block = eth.get_block('1048579')
assert(err == nil)
assert(block.number == '0x100003')