sys = require_plugin('sys')

flow = fail_on_error(sys.flows.create({ name = 'flow-1' }))
sys.await_completed(flow)

func_one = fail_on_error(sys.funcs.create({ flow_id = flow.id; name = 'test-func'; inputs = {}; code = [[ x = 4 + 2]] }))
sys.await_completed(func_one)

-- trigger name is unique
trigger = fail_on_error(sys.triggers.create_fixed_rate({
    func_id = func_one.id,
    flow_id = '1',
    name = 'trigger-to-create',
    inputs = { },
    duration = 'PT5S'
}))
sys.await_completed(trigger)

trigger = fail_on_error(sys.triggers.create_fixed_rate({
    func_id = func_one.id,
    flow_id = '1',
    name = 'trigger-to-create',
    inputs = { },
    duration = 'PT5S'
}))
assert(sys.await_failed(trigger) == nil)

_, triggers = sys.triggers.list()
assert(#triggers == 1)
--
-- same name different flow
trigger = fail_on_error(sys.triggers.create_fixed_rate({
    func_id = func_one.id,
    flow_id = flow.id,
    name = 'trigger-to-create',
    inputs = { },
    duration = 'PT5S'
}))
sys.await_completed(trigger)
--
_, triggers = sys.triggers.list()
assert(#triggers == 2)
