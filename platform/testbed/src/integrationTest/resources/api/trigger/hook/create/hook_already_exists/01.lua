sys = require_plugin('sys')

namespace = fail_on_error(sys.funcs.create({ name = 'test-func-t'; inputs = {}; code = [[4 + 2]] }))
sys.await_completed(namespace)

hook_req = fail_on_error(sys.hooks.create({ name = "some-amazing-hook-t" }))
sys.await(hook_req)

req_one = fail_on_error(sys.triggers.create_hook({
    func_id = namespace.func_id,
    name = 'trigger-one',
    inputs = { },
    hook_id = hook_req.hook_id,
    hook_method = 'Get'
}))
sys.await_completed(req_one)

req_two = fail_on_error(sys.triggers.create_hook({
    func_id = namespace.func_id,
    name = 'trigger-two',
    inputs = { },
    hook_id = hook_req.hook_id,
    hook_method = 'Get'
}))
sys.await_failed(req_two)

triggers = fail_on_error(sys.triggers.list())
assert(#triggers == 1)