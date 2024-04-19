sys = require_plugin('std.sys')

--hook name is unique
hook_req = fail_on_error(sys.hooks.create({ namespace_id = '539'; name = 'hook-name' }))
sys.await_completed(hook_req)
assert(hook_req ~= nil)

_, hook_req = sys.hooks.create({ namespace_id = '539'; name = 'hook-name' })
assert(sys.await_failed(hook_req) == nil)
assert(hook_req ~= nil)

_, hooks = sys.hooks.list()
assert(#hooks == 1)

-- same name different namespace
namespace = fail_on_error(sys.namespaces.append({ name = 'namespace-1' }))
sys.await_completed(namespace)

hook_req = fail_on_error(sys.hooks.create({ name = 'hook-name', namespace_id = namespace.id }))
sys.await_completed(hook_req)

_, hooks = sys.hooks.list()
assert(#hooks == 2)