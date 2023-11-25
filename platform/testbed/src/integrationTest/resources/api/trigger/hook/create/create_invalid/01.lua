sys = require('sys')

func_req = fail_on_error(sys.funcs.create({ name = 'test-func'; inputs = {}; code = [[4 + 2]] }))
sys.await_completed(func_req)

hook_req = fail_on_error(sys.hooks.create({ name = "some-amazing-hook" }))
sys.await_completed(hook_req)

trigger_req = fail_on_error(sys.triggers.create_hook({
    func_id = func_req.func_id,
    flow_id = '1',
    name = 'trigger-one',
    inputs = { },
    hook_id = hook_req.hook_id,
    hook_method = 'GET'
}))
sys.await_completed(trigger_req)

err, trigger = sys.triggers.create_hook({
    func_id = func_req.func_id,
    flow_id = '1',
    name = 'trigger-two',
    inputs = { },
    hook_id = hook_req.hook_id,
    hook_method = 'GET'
})
assert(err.message == 'Trigger already exists')
assert(trigger == nil)

_, triggers = sys.triggers.list()
assert(#triggers == 1)

