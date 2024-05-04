--local eth = require("web3.eth")
--local log = require("log")
--
--eth.config.update({
--    host = 'http://web3-eth-proxy-1:10000'
--})
--
--local next_block_number = context.state.next_block_number
--if next_block_number == nil then
--    next_block_number = 0
--end
--
--local step_size = 100
--local requests = {}
--for idx = 1, step_size do
--    table.insert(requests, { type = "get_block", block = next_block_number + idx })
--end
--
--local err, batch_result = eth.execute(requests)
--
--if err ~= nil then
--    log.error(err.message)
--else
--    for _, block in pairs(batch_result) do
--        log.info("Added: " .. block.number .. " " .. block.hash)
--        -- context.emit({ topic = "eth::block_available", block = block.id })
--    end
--    context.state.next_block_number = next_block_number + step_size
--end
--
--

evm = require_plugin("web3.evm")

log = body = .create({})

step_size = 25
next_block_number = context.state.next_block_number or 19086500

requests = {}
for i = 1, step_size do
    table.insert(requests, evm.get_block(next_block_number + i))
end

context.state.next_block_number = next_block_number + step_size

res = fail_on_error(evm.execute({
    url = 'http://localhost:10000/eth',
    requests = requests
}))

for _, response in ipairs(res) do
    log.info(response.result.number)
end

-- for k,v in pairs(res[1].result) do
--     print(k,v)
-- end



--eth = require("web3.eth").create({
--    url = 'http://web3-proxy-1:10000/eth'
--})
--
--log = body = .create({})
--
--step_size = 25
--next_block_number = context.state.next_block_number or 19086500
--
--blocks = {}
--for i = 1, step_size do
--    table.insert(blocks, next_block_number + i)
--end
--
--context.state.next_block_number = next_block_number + step_size
--
--blocks = fail_on_error(eth.get_blocks(blocks))
--
--for _, block in ipairs(blocks) do
--    log.info(block.number)
--end
