arbitrum = require('web3.arbitrum').create({
    url = context.env.test_url
})

err, blocks = arbitrum.get_blocks({ 0x100003, 1048579, '0x100003', '1048579' })
assert(err == nil)
assert(#blocks == 4)

for _, block in ipairs(blocks) do
    assert(block.number == '0x100003')
end

