sys = require('sys')

func_req = fail_on_error(sys.func.create({ name = 'test-func'; inputs = {}; code = [[4 + 2]] }))
sys.await_completed(func_req)

topic_req = fail_on_error(sys.topic.create({ name = "some-amazing-topic" }))
sys.await(topic_req)

trigger = fail_on_error(sys.trigger.create_event({
    func_id = func_req.func_id,
    name = 'trigger-to-create',
    inputs = { },
    topic_id = topic_req.topic_id
}))
sys.await_completed(trigger)

assert(trigger.id ~= nil)
assert(trigger.status == 'Submitted')
assert(trigger.trigger_id ~= nil)
assert(trigger.group_id == '1')
assert(trigger.namespace_id == '1')

trigger = fail_on_error(sys.trigger.get(trigger.id))

assert(trigger.type == 'Event')
assert(trigger.name == 'trigger-to-create')
assert(trigger.func.name == "test-func")
assert(trigger.namespace.name == "root-namespace")
assert(trigger.topic.name == "some-amazing-topic")

