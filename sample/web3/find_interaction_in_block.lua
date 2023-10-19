local block_id = context.events[1].block
-- local block = 10915074


local hml = require('web3.hml')
hml.config.update({
    host = "http://localhost:8000"
})

local err, block = hml.get_block(block_id)
if err ~= nil then
    print(err.message)
else
    print(block)
    for _, v in pairs(block.transactions) do
        print(v.from, '-->', v.to)
    end
end