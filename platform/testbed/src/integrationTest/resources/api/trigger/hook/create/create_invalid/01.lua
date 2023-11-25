sys = require('sys')

func_req = fail_on_error(sys.funcs.create({ name = 'test-func'; inputs = {}; code = [[4 + 2]] }))
sys.await_completed(func_req)

hook_req = fail_on_error(sys.hooks.create({ name = "some-amazing-hook" }))
sys.await(hook_req)

trigger_req = fail_on_error(sys.triggers.create_hook({
    func_id = func_req.func_id,
    flow_id = '1',
    name = 'hook-trigger',
    inputs = { },
    hook_id = hook_req.hook_id,
    hook_method = 'GET'
}))
sys.await_completed(trigger_req)

trigger = fail_on_error(sys.triggers.create_hook({
    func_id = func_req.func_id,
    flow_id = '1',
    name = 'hook-trigger2',
    inputs = { },
    hook_id = hook_req.hook_id,
    hook_method = 'GET'
}))
assert(sys.await_failed(trigger) == nil)

_, triggers = sys.triggers.list()
assert(#triggers == 1)