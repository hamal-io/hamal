sys = require('sys')

--hook name is unique
hook_req = fail_on_error(sys.hook.create({ namespace_id = '1'; name = 'hook-name' }))
sys.await_completed(hook_req)
assert(hook_req ~= nil)

_, hook_req = sys.hook.create({ namespace_id = '1'; name = 'hook-name' })
assert(sys.await_failed(hook_req) == nil)
assert(hook_req ~= nil)

_, funcs = sys.hook.list()
assert(#funcs == 1)

-- same name different namespace
namespace_req = fail_on_error(sys.namespace.create({ name = 'namespace-1' }))
sys.await_completed(namespace_req)

hook_req = fail_on_error(sys.hook.create({ name = 'hook-name', namespace_id = namespace_req.id }))
sys.await_completed(hook_req)

_, funcs = sys.hook.list()
assert(#funcs == 2)