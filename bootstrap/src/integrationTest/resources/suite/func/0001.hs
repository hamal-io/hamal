local log = require('log')
local sys = require('sys')

---@type string
local func_id = sys.func.create({
    name = 'empty-test-func'
})

--local test = require('integration-test')
--test._cfg.timeoutInSeconds = 30
--test.assert()
--test.fail()
--test.complete()

local funcs = sys.func.list()
log.info(funcs)

local func = funcs[1]
assert(func.id == func_id)
assert(func.name == 'empty-test-func')

local topics = sys.topic.list()
local topic = topics[1]
log.info(topic)

local test = require('test')
test.complete()
test.fail()

local topic_id = topic.id
local emitter = sys.evt.emitter(topic_id)
emitter.emit({})
