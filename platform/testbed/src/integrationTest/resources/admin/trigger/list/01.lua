sys = require('sys')

func_req = fail_on_error(sys.func.create({ namespace_id = '1', name = 'empty-test-func'; inputs = {}; code = [[4 + 2]] }))
sys.await_completed(func_req)

topic_req = fail_on_error(sys.topic.create({ name = "some-amazing-topic" }))
sys.await(topic_req)

triggers = fail_on_error(sys.trigger.list())
assert(#triggers == 0)

trigger_req = fail_on_error(sys.trigger.create_event({
    func_id = func_req.id,
    namespace_id = '1',
    name = 'trigger-1',
    inputs = { },
    topic_id = topic_req.id
}))
sys.await_completed(trigger_req)

triggers = fail_on_error(sys.trigger.list())
assert(#triggers == 1)

triggers = fail_on_error(sys.trigger.list({}))
assert(#triggers == 1)

assert(trigger_req.id == triggers[1].id)
assert(triggers[1].name == 'trigger-1')

trigger_two = fail_on_error(sys.trigger.create_fixed_rate({
    func_id = func_req.id,
    namespace_id = '1',
    name = 'trigger-to-create',
    inputs = { },
    duration = 'PT5S'
}))
sys.await_completed(trigger_two)

triggers = fail_on_error(sys.trigger.list())
assert(#triggers == 2)
