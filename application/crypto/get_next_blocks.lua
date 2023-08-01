local eth = web3.eth

--eth.__config.update({
--    host = 'http://localhost:8000'
--})

local step_size = 6

local next_block_number = ctx.state.next_block_number

if next_block_number == nil then
    --    next_block_number = 10000000
    next_block_number = 10390254
end

local err, batch_result = eth.execute({
    eth.request.get_block(next_block_number + 1),
    eth.request.get_block(next_block_number + 2),
    eth.request.get_block(next_block_number + 3),
    eth.request.get_block(next_block_number + 4),
    eth.request.get_block(next_block_number + 5),
    eth.request.get_block(next_block_number + 6)
})

for _, block in pairs(batch_result) do
    print(block.id, block.hash)
end

if err == nil then
    ctx.state.next_block_number = next_block_number + step_size
end
