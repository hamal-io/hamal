sys = require('sys')
--
_, namespace_req = sys.namespace.create({ name = "hamal::name:space::rocks" })
sys.await_completed(namespace_req)

err, hook_req = sys.hook.create({ name = 'hook-1' })
sys.await_completed(hook_req)

err, hook_req = sys.hook.create({ namespace_id = namespace_req.id, name = 'hook-1' })
sys.await_completed(hook_req)

assert(err == nil)
assert(hook_req ~= nil)

_, hook = sys.hook.get(hook_req.id)
assert(hook.namespace.id == namespace_req.id)
assert(hook.namespace.name == "hamal::name:space::rocks")

err, funcs = sys.hook.list()
print(#funcs)
assert(#funcs == 2)