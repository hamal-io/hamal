sys = require_plugin('sys')
--
namespace = fail_on_error(sys.namespaces.create({ name = "hamal::namespace::rocks" }))
sys.await_completed(namespace)

hook = fail_on_error(sys.hooks.create({
    namespace_id = namespace.id,
    name = 'hook-1'
}))
sys.await_completed(hook)

assert(hook ~= nil)
assert(hook.workspace_id == '1')
assert(hook.namespace_id == namespace.id)

_, hook = sys.hooks.get(hook.id)
assert(hook.namespace.id == namespace.id)
assert(hook.namespace.name == "hamal::namespace::rocks")

err, hooks = sys.hooks.list()
assert(#hooks == 1)