sys = require_plugin('sys')
--
namespace = fail_on_error(sys.namespaces.append({ name = "hamal::namespace::rocks" }))
sys.await_completed(namespace)

hook_req = fail_on_error(sys.hooks.create({
    namespace_id = namespace.id,
    name = 'hook-1'
}))
sys.await_completed(hook_req)

assert(hook_req ~= nil)
assert(hook_req.workspace_id == '539')
assert(hook_req.namespace_id == namespace.id)

_, hook_req = sys.hooks.get(hook_req.id)
assert(hook_req.namespace.id == namespace.id)
assert(hook_req.namespace.name == "hamal::namespace::rocks")

err, hooks = sys.hooks.list()
assert(#hooks == 1)