sys = require('sys')
--
namespace = fail_on_error(sys.namespace.create({ name = "hamal::name:space::rocks" }))
sys.await_completed(namespace)

hook_req = fail_on_error(sys.hook.create({
    namespace_id = namespace.id,
    name = 'hook-1'
}))
sys.await_completed(hook_req)

assert(hook_req ~= nil)
assert(hook_req.group_id == '1')
assert(hook_req.namespace_id == namespace.id)

_, hook = sys.hook.get(hook_req.id)
assert(hook.namespace.id == namespace.id)
assert(hook.namespace.name == "hamal::name:space::rocks")

err, hooks = sys.hook.list()
assert(#hooks == 1)