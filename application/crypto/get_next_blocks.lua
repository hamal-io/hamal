local eth = require("web3.eth")

eth.config.update({
    host = 'http://localhost:9000'
})

local step_size = 2

local next_block_number = ctx.state.next_block_number

if next_block_number == nil then
    next_block_number = 10726626
end

local err, batch_result = eth.execute({
    {type="get_block", block = next_block_number + 1},
    {type="get_block", block = next_block_number + 2},
})

if err ~= nil then
    print("ERROR:", err.message)
else
    for _, block in pairs(batch_result) do
        print(block.id, block.hash)
    end
    ctx.state.next_block_number = next_block_number + step_size
end
