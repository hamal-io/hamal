sys = require_plugin('sys')

func_req = fail_on_error(sys.funcs.create({ name = 'test-func'; inputs = {}; code = [[4 + 2]] }))
sys.await_completed(func_req)

hook_req = fail_on_error(sys.hooks.create({ name = "some-amazing-hook" }))
sys.await(hook_req)

req_one = fail_on_error(sys.triggers.create_hook({
    func_id = func_req.id,
    name = 'hook-trigger',
    inputs = { },
    hook_method = 'Get',
    hook_id = hook_req.id
}))
sys.await_completed(req_one)

assert(req_one.request_id ~= nil)
assert(req_one.request_status == 'Submitted')
assert(req_one.id ~= nil)
assert(req_one.workspace_id == '539')
assert(req_one.namespace_id == '539')

req_two = fail_on_error(sys.triggers.get(req_one.id))
assert(req_two.type == 'Hook')
assert(req_two.name == 'hook-trigger')
assert(req_two.func.name == "test-func")
assert(req_two.namespace.id == '539')
assert(req_two.namespace.name == "root-namespace")
assert(req_two.hook.name == "some-amazing-hook")

