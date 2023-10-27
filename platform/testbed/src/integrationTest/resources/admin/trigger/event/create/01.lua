sys = require('sys')

func_req = fail_on_error(sys.func.create({ namespace_id = '1', name = 'empty-test-func'; inputs = {}; code = [[4 + 2]] }))
sys.await_completed(func_req)

_, topic_req = sys.topic.create({ name = "some-amazing-topic" })
sys.await(topic_req)

trigger_req = fail_on_error(sys.trigger.create_event({
    func_id = func_req.id,
    namespace_id = '1',
    name = 'trigger-to-create',
    inputs = { },
    topic_id = topic_req.id
}))
sys.await_completed(trigger_req)

assert(trigger_req.req_id ~= nil)
assert(trigger_req.status == 'Submitted')
assert(trigger_req.id ~= nil)

err, trigger = sys.trigger.get(trigger_req.id)
assert(err == nil)

assert(trigger.type == 'Event')
assert(trigger.name == 'trigger-to-create')
assert(trigger.func.name == "empty-test-func")
assert(trigger.namespace.name == "hamal")
assert(trigger.topic.name == "some-amazing-topic")

err, triggers = sys.trigger.list()
assert(err == nil)
assert(#triggers == 1)

assert(triggers[1].type == 'Event')
assert(triggers[1].name == 'trigger-to-create')
assert(triggers[1].func.name == 'empty-test-func')
assert(triggers[1].namespace.name == "hamal")
assert(triggers[1].topic.name == "some-amazing-topic")