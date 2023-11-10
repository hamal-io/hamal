sys = require('sys')

flow = fail_on_error(sys.flows.create({ name = 'flow-1' }))
sys.await_completed(flow)

func = fail_on_error(sys.funcs.create({ flow_id = flow.id; name = 'test-func'; inputs = {}; code = [[4 + 2]] }))
sys.await_completed(func)

_, topic_req = sys.topics.create({ name = "some-amazing-topic" })
sys.await(topic_req)

-- trigger name is unique
trigger = fail_on_error(sys.triggers.create_event({
    func_id = func.id,
    flow_id = '1',
    name = 'trigger-to-create',
    inputs = { },
    topic_id = topic_req.id
}))
sys.await_completed(trigger)

trigger = fail_on_error(sys.triggers.create_event({
    func_id = func.id,
    flow_id = '1',
    name = 'trigger-to-create',
    inputs = { },
    topic_id = topic_req.id
}))
assert(sys.await_failed(trigger) == nil)

_, triggers = sys.triggers.list()
assert(#triggers == 1)

-- same name different flow
trigger = fail_on_error(sys.triggers.create_event({
    func_id = func.id,
    flow_id = flow.id,
    name = 'trigger-to-create',
    inputs = { },
    topic_id = topic_req.id
}))
sys.await_completed(trigger)

_, triggers = sys.triggers.list()
assert(#triggers == 2)