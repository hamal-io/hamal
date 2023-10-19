local eth = require("web3.eth")
local log = require("log")

eth.config.update({
    host = 'http://web3-eth-proxy-1:10000'
})

local next_block_number = context.state.next_block_number
if next_block_number == nil then
    next_block_number = 0
end

local step_size = 100
local requests = {}
for idx = 1, step_size do
    table.insert(requests, { type = "get_block", block = next_block_number + idx })
end

local err, batch_result = eth.execute(requests)

if err ~= nil then
    log.error(err.message)
else
    for _, block in pairs(batch_result) do
        log.info("Added: " .. block.number .. " " .. block.hash)
        -- context.emit({ topic = "eth::block_available", block = block.id })
    end
    context.state.next_block_number = next_block_number + step_size
end
