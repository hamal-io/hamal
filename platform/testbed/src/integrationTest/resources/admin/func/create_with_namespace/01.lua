sys = require('sys')
--
_, namespace_req = sys.namespace.create({ name = "hamal::name:space::rocks" })
sys.await_completed(namespace_req)

err, func_req = sys.func.create({ name = 'func-1' })
sys.await_completed(func_req)

err, func_req = sys.func.create({ namespace_id = namespace_req.id, name = 'func-1' })
sys.await_completed(func_req)

assert(err == nil)
assert(func_req ~= nil)

_, func = sys.func.get(func_req.id)
assert(func.namespace.id == namespace_req.id)
assert(func.namespace.name == "hamal::name:space::rocks")

err, hooks = sys.func.list()
print(#hooks)
assert(#hooks == 2)