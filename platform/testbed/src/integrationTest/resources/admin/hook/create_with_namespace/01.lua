sys = require('sys')
--
hook_req = fail_on_error(sys.hook.create({ namespace_id = '1'; name = 'hook-1' }))
sys.await_completed(hook_req)

namespace_req = fail_on_error(sys.namespace.create({ name = "hamal::name:space::rocks" }))
sys.await_completed(namespace_req)

hook_req = fail_on_error(sys.hook.create({ namespace_id = namespace_req.id, name = 'hook-1' }))
sys.await_completed(hook_req)

assert(hook_req ~= nil)

_, hook = sys.hook.get(hook_req.id)
assert(hook.namespace.id == namespace_req.id)
assert(hook.namespace.name == "hamal::name:space::rocks")

err, funcs = sys.hook.list()
print(#funcs)
assert(#funcs == 2)