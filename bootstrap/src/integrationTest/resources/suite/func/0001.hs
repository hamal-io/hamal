local log = require('log')
local sys = require('sys')
local test = require('test')

local func_id = sys.func.create({
    name = 'empty-test-func'
})

local funcs = sys.func.list()
log.info(funcs)

local func = funcs[1]
test.assert(func.id ~= func_id)
test.assert(func.name == 'empty-test-func')

local topics = sys.topic.list()
local topic = topics[1]
log.info(topic)

test.complete()