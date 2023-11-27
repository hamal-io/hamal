sys = require_plugin('sys')

func = fail_on_error(sys.funcs.create({ name = 'test-func-t'; inputs = {}; code = [[4 + 2]] }))
sys.await_completed(func)

hook = fail_on_error(sys.hooks.create({ name = "some-amazing-hook-t" }))
sys.await(hook)

trigger_req = fail_on_error(sys.triggers.create_hook({
    func_id = func.func_id,
    name = 'trigger-one',
    inputs = { },
    hook_id = hook.hook_id,
    hook_method = 'Get'
}))
sys.await_completed(trigger_req)


err, trigger = sys.triggers.create_hook({
    func_id = func.func_id,
    name = 'trigger-two',
    inputs = { },
    hook_id = hook.hook_id,
    hook_method = 'Get'
})

assert(err.message == 'Trigger already exists')
assert(trigger == nil)

triggers = fail_on_error(sys.triggers.list())
assert(#triggers == 1)