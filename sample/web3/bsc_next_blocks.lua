local bsc = require("web3.eth")
local log = require("log")

bsc.config.update({
    host = 'http://bsc-proxy:8000'
})

local step_size = 2

local next_block_number = context.state.next_block_number

if next_block_number == nil then
    next_block_number = 0
end

local requests = {}
for idx = 1, step_size do
    table.insert(requests, { type = "get_block", block = next_block_number + idx })
end

local err, batch_result = bsc.execute(requests)
if err ~= nil then
    log.error(err.message)
else
    for _, block in pairs(batch_result) do
        log.info("Added: " .. block.id .. " " .. block.hash)
        context.emit({ topic = "bsc::block_available", block = block.id })
    end
    context.state.next_block_number = next_block_number + step_size
end
