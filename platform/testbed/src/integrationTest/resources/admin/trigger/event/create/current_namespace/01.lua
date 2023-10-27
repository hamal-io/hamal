sys = require('sys')

func = fail_on_error(sys.func.create({ name = 'test-func'; inputs = {}; code = [[4 + 2]] }))
sys.await_completed(func)

topic_req = fail_on_error(sys.topic.create({ name = "some-amazing-topic" }))
sys.await(topic_req)

trigger = fail_on_error(sys.trigger.create_event({
    func_id = func.id,
    name = 'trigger-to-create',
    inputs = { },
    topic_id = topic_req.id
}))
sys.await_completed(trigger)

assert(trigger.req_id ~= nil)
assert(trigger.status == 'Submitted')
assert(trigger.id ~= nil)
assert(trigger.group_id == '1')
assert(trigger.namespace_id == '1')

err, trigger = sys.trigger.get(trigger.id)
assert(err == nil)

assert(trigger.type == 'Event')
assert(trigger.name == 'trigger-to-create')
assert(trigger.func.name == "test-func")
assert(trigger.namespace.name == "root-namespace")
assert(trigger.topic.name == "some-amazing-topic")

