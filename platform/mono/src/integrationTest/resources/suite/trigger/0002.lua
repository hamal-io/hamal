local sys = require('sys')

local _, create_func_req = sys.func.create({ name = 'empty-test-func'; inputs = {}; code = [[4 + 2]] })
sys.await_completed(create_func_req)

local _, topic_one_req = sys.topic.create({ name = "some-amazing-topic" })
sys.await(topic_one_req)

local err, create_trigger_req = sys.trigger.create_event({
    func_id = create_func_req.id,
    namespace_id = nil,
    name = 'trigger-to-create',
    inputs = { },
    topic_id = topic_one_req.id
})
assert(err == nil)
sys.await_completed(create_trigger_req)

assert(create_trigger_req.req_id ~= nil)
assert(create_trigger_req.status == 'Submitted')
assert(create_trigger_req.id ~= nil)

local err, trigger = sys.trigger.get(create_trigger_req.id)
assert(err == nil)

assert(trigger.type == 'Event')
assert(trigger.name == 'trigger-to-create')
assert(trigger.func.name == "empty-test-func")
assert(trigger.namespace.name == "hamal")
assert(trigger.topic.name == "some-amazing-topic")

local err, triggers = sys.trigger.list()
assert(err == nil)
assert(#triggers == 1)

assert(triggers[1].type == 'Event')
assert(triggers[1].name == 'trigger-to-create')
assert(triggers[1].func.name == 'empty-test-func')
assert(triggers[1].namespace.name == "hamal")
assert(triggers[1].topic.name == "some-amazing-topic")