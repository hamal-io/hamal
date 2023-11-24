sys = require('sys')

flow = fail_on_error(sys.flows.create({ name = 'flow-1' }))
sys.await_completed(flow)

func = fail_on_error(sys.funcs.create({ flow_id = flow.id; name = 'test-func'; inputs = {}; code = [[4 + 2]] }))
sys.await_completed(func)

hook = fail_on_error(sys.hooks.create({ flow_id = '1'; name = "some-amazing-hook" }))
sys.await(hook)

-- trigger name is unique
trigger = fail_on_error(sys.triggers.create_hook({
    func_id = func.id,
    flow_id = '1',
    name = 'trigger-to-create',
    inputs = { },
    hook_id = hook.id,
    hook_method = 'GET'

}))
sys.await_completed(trigger)

trigger = fail_on_error(sys.triggers.create_hook({
    func_id = func.id,
    flow_id = '1',
    name = 'trigger-to-create',
    inputs = { },
    hook_id = hook.id,
    hook_method = 'POST'
}))
assert(sys.await_failed(trigger) == nil)

_, triggers = sys.triggers.list()
assert(#triggers == 1)

-- same name different flow
err, trigger = sys.triggers.create_hook({
    func_id = func.id,
    flow_id = flow.id,
    name = 'trigger-to-create',
    inputs = { },
    hook_id = hook.id,
    hook_method = 'PATCH'
})
assert(err == nil)
sys.await_completed(trigger)

_, triggers = sys.triggers.list()
assert(#triggers == 2)
