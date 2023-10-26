sys = require('sys')

create_func_req = fail_on_error(sys.func.create({ namespace_id = '1', name = 'empty-test-func'; inputs = {}; code = [[4 + 2]] }))
sys.await_completed(create_func_req)

_, topic_one_req = sys.topic.create({ name = "some-amazing-topic" })
sys.await(topic_one_req)

trigger_create_req = fail_on_error(sys.trigger.create_event({
    func_id = create_func_req.id,
    namespace_id = '1',
    name = 'trigger-to-create',
    inputs = { },
    topic_id = topic_one_req.id
}))
sys.await_completed(trigger_create_req)

assert(trigger_create_req.req_id ~= nil)
assert(trigger_create_req.status == 'Submitted')
assert(trigger_create_req.id ~= nil)

err, trigger = sys.trigger.get(trigger_create_req.id)
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