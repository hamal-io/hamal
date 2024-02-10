sys = require_plugin('sys')

namespace_req = fail_on_error(sys.namespaces.create({ name = "hamal::namespace::rocks" }))
sys.await_completed(namespace_req)

func_req = fail_on_error(sys.funcs.create({ namespace_id = namespace_req.id, name = 'test-func'; inputs = {}; code = [[4 + 2]] }))
sys.await_completed(func_req)

hook = fail_on_error(sys.hooks.create({ namespace_id = namespace_req.id, name = "some-amazing-hook" }))
sys.await(hook)

req_one = fail_on_error(sys.triggers.create_hook({
    namespace_id = namespace_req.id,
    func_id = func_req.id,
    name = 'hook-trigger',
    inputs = { },
    hook_method = 'Get',
    hook_id = hook.id
}))
sys.await_completed(req_one)

assert(req_one.id ~= nil)
assert(req_one.status == 'Submitted')
assert(req_one.trigger_id ~= nil)
assert(req_one.group_id == '1')
assert(req_one.namespace_id == namespace_req.namespace_id)

req_two = fail_on_error(sys.triggers.get(req_one.trigger_id))
assert(req_two.type == 'Hook')
assert(req_two.name == 'hook-trigger')
assert(req_two.func.name == "test-func")
assert(req_two.namespace.id == namespace_req.id)
assert(req_two.namespace.name == "hamal::namespace::rocks")
assert(req_two.hook.name == "some-amazing-hook")

