sys = require_plugin('sys')

func_req = fail_on_error(sys.funcs.create({ name = 'test-func'; inputs = {}; code = [[4 + 2]] }))
sys.await_completed(func_req)

topic_req = fail_on_error(sys.topics.create({ name = "some-amazing-topic" }))
sys.await(topic_req)

trigger_req = fail_on_error(sys.triggers.create_event({
    func_id = func_req.func_id,
    name = 'trigger-to-create',
    inputs = { },
    topic_id = topic_req.topic_id
}))
sys.await_completed(trigger_req)

triggers = fail_on_error(sys.triggers.list())
assert(#triggers == 1)

trigger = triggers[1]
assert(trigger.type == 'Event')
assert(trigger.name == 'trigger-to-create')
assert(trigger.func.name == "test-func")
assert(trigger.flow.id == '1')
assert(trigger.flow.name == "root-flow")
assert(trigger.topic.name == "some-amazing-topic")