sys = require('sys')

func = fail_on_error(sys.funcs.create({ flow_id = '1', name = 'test-func'; inputs = {}; code = [[4 + 2]] }))
sys.await_completed(func)

topic_req = fail_on_error(sys.topics.create({ name = "some-amazing-topic" }))
sys.await(topic_req)

triggers = fail_on_error(sys.triggers.list())
assert(#triggers == 0)

trigger = fail_on_error(sys.triggers.create_event({
    func_id = func.id,
    flow_id = '1',
    name = 'trigger-1',
    inputs = { },
    topic_id = topic_req.id
}))
sys.await_completed(trigger)

triggers = fail_on_error(sys.triggers.list())
assert(#triggers == 1)

triggers = fail_on_error(sys.triggers.list({}))
assert(#triggers == 1)

assert(trigger.id == triggers[1].id)
assert(triggers[1].name == 'trigger-1')

trigger_two = fail_on_error(sys.triggers.create_fixed_rate({
    func_id = func.id,
    flow_id = '1',
    name = 'trigger-to-create',
    inputs = { },
    duration = 'PT5S'
}))
sys.await_completed(trigger_two)

triggers = fail_on_error(sys.triggers.list())
assert(#triggers == 2)
