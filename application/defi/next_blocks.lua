local eth = require("web3.eth")
local log = require("log")

eth.config.update({
    host = 'http://eth-proxy:8000'
})

local step_size = 25

local next_block_number = ctx.state.next_block_number

if next_block_number == nil then
    next_block_number = 11591500
end

local err, batch_result = eth.execute({
    { type = "get_block", block = next_block_number + 1 },
    { type = "get_block", block = next_block_number + 2 },
    { type = "get_block", block = next_block_number + 3 },
    { type = "get_block", block = next_block_number + 4 },
    { type = "get_block", block = next_block_number + 5 },
    { type = "get_block", block = next_block_number + 6 },
    { type = "get_block", block = next_block_number + 7 },
    { type = "get_block", block = next_block_number + 8 },
    { type = "get_block", block = next_block_number + 9 },
    { type = "get_block", block = next_block_number + 10 },
    { type = "get_block", block = next_block_number + 11 },
    { type = "get_block", block = next_block_number + 12 },
    { type = "get_block", block = next_block_number + 13 },
    { type = "get_block", block = next_block_number + 14 },
    { type = "get_block", block = next_block_number + 15 },
    { type = "get_block", block = next_block_number + 16 },
    { type = "get_block", block = next_block_number + 17 },
    { type = "get_block", block = next_block_number + 18 },
    { type = "get_block", block = next_block_number + 19 },
    { type = "get_block", block = next_block_number + 20 },
    { type = "get_block", block = next_block_number + 21 },
    { type = "get_block", block = next_block_number + 22 },
    { type = "get_block", block = next_block_number + 23 },
    { type = "get_block", block = next_block_number + 24 },
    { type = "get_block", block = next_block_number + 25 }
})

if err ~= nil then
    log.error( err.message)
else
    for _, block in pairs(batch_result) do
        log.info("Added: " .. block.id .. " " .. block.hash)
        ctx.emit({ topic = "eth::block_available", block = block.id })
    end
    ctx.state.next_block_number = next_block_number + step_size
end
