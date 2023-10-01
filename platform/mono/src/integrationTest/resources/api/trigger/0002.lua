admin = require('sys')

_, create_func_req = admin.func.create({ name = 'empty-test-func'; inputs = {}; code = [[4 + 2]] })
admin.await_completed(create_func_req)

_, topic_one_req = admin.topic.create({ name = "some-amazing-topic" })
admin.await(topic_one_req)

err, create_trigger_req = admin.trigger.create_event({
    func_id = create_func_req.id,
    namespace_id = nil,
    name = 'trigger-to-create',
    inputs = { },
    topic_id = topic_one_req.id
})
assert(err == nil)
admin.await_completed(create_trigger_req)

assert(create_trigger_req.req_id ~= nil)
assert(create_trigger_req.status == 'Submitted')
assert(create_trigger_req.id ~= nil)

err, trigger = admin.trigger.get(create_trigger_req.id)
assert(err == nil)

assert(trigger.type == 'Event')
assert(trigger.name == 'trigger-to-create')
assert(trigger.func.name == "empty-test-func")
assert(trigger.namespace.name == "hamal")
assert(trigger.topic.name == "some-amazing-topic")

err, triggers = admin.trigger.list()
assert(err == nil)
assert(#triggers == 1)

assert(triggers[1].type == 'Event')
assert(triggers[1].name == 'trigger-to-create')
assert(triggers[1].func.name == 'empty-test-func')
assert(triggers[1].namespace.name == "hamal")
assert(triggers[1].topic.name == "some-amazing-topic")