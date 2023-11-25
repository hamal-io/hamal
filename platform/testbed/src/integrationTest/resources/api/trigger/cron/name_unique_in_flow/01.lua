sys = require('sys')

flow = fail_on_error(sys.flows.create({ name = 'flow-1' }))
sys.await_completed(flow)

func_one = fail_on_error(sys.funcs.create({ flow_id = flow.id; name = 'test-func'; inputs = {}; code = [[4 + 2]] }))
sys.await_completed(func_one)

_, topic_req = sys.topics.create({ name = "some-amazing-topic" })
sys.await(topic_req)

-- trigger name is unique
trigger = fail_on_error(sys.triggers.create_cron({
    func_id = func_one.id,
    flow_id = '1',
    name = 'trigger-to-create',
    inputs = { },
    cron = '0 0 8-10 * * *'
}))
sys.await_completed(trigger)

trigger = fail_on_error(sys.triggers.create_cron({
    func_id = func_one.id,
    flow_id = '1',
    name = 'trigger-to-create',
    inputs = { },
    cron = '0 0 8-10 * * *'
}))
assert(sys.await_failed(trigger) == nil)

_, triggers = sys.triggers.list()
assert(#triggers == 1)

-- same name different flow
trigger = fail_on_error(sys.triggers.create_cron({
    func_id = func_one.id,
    flow_id = flow.id,
    name = 'trigger-to-create',
    inputs = { },
    cron = '0 0 8-10 * * *'
}))
sys.await_completed(trigger)

_, triggers = sys.triggers.list()
assert(#triggers == 2)