sys = require_plugin('sys')

flow = fail_on_error(sys.flows.create({ name = 'flow-1' }))
sys.await_completed(flow)

func_one = fail_on_error(sys.funcs.create({ flow_id = flow.id; name = 'test-func'; inputs = {}; code = [[4 + 2]] }))
sys.await_completed(func_one)

hook = fail_on_error(sys.hooks.create({ flow_id = '1'; name = "some-amazing-hook" }))
sys.await(hook)

-- trigger name is unique
req_two = fail_on_error(sys.triggers.create_hook({
    func_id = func_one.id,
    flow_id = '1',
    name = 'trigger-to-create',
    inputs = { },
    hook_id = hook.id,
    hook_method = 'Get'
}))
sys.await_completed(req_two)

req_two = fail_on_error(sys.triggers.create_hook({
    func_id = func_one.id,
    flow_id = '1',
    name = 'trigger-to-create',
    inputs = { },
    hook_id = hook.id,
    hook_method = 'Post'
}))
assert(sys.await_failed(req_two) == nil)

_, triggers = sys.triggers.list()
assert(#triggers == 1)

-- same name different flow
err, req_two = sys.triggers.create_hook({
    func_id = func_one.id,
    flow_id = flow.id,
    name = 'trigger-to-create',
    inputs = { },
    hook_id = hook.id,
    hook_method = 'Patch'
})
assert(err == nil)
sys.await_completed(req_two)

_, triggers = sys.triggers.list()
assert(#triggers == 2)