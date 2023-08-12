local eth = require("web3.eth")
local log = require("log")

eth.config.update({
    host = 'http://eth-proxy:8000'
})

local step_size = 50

local next_block_number = ctx.state.next_block_number

if next_block_number == nil then
    next_block_number = 14494151
end

local requests = {}
for idx=1,step_size do
    table.insert(requests,   { type = "get_block", block = next_block_number + idx })
end


local err, batch_result = eth.execute(requests)

if err ~= nil then
    log.error( err.message)
else
    for _, block in pairs(batch_result) do
        log.info("Added: " .. block.id .. " " .. block.hash)
        ctx.emit({ topic = "eth::block_available", block = block.id })
    end
    ctx.state.next_block_number = next_block_number + step_size
end
