sys = require_plugin('sys')

--hook name is unique
hook = fail_on_error(sys.hooks.create({ namespace_id = '1'; name = 'hook-name' }))
sys.await_completed(hook)
assert(hook ~= nil)

_, hook = sys.hooks.create({ namespace_id = '1'; name = 'hook-name' })
assert(sys.await_failed(hook) == nil)
assert(hook ~= nil)

_, hooks = sys.hooks.list()
assert(#hooks == 1)

-- same name different namespace
namespace = fail_on_error(sys.namespaces.append({ name = 'namespace-1' }))
sys.await_completed(namespace)

hook = fail_on_error(sys.hooks.create({ name = 'hook-name', namespace_id = namespace.id }))
sys.await_completed(hook)

_, hooks = sys.hooks.list()
assert(#hooks == 2)