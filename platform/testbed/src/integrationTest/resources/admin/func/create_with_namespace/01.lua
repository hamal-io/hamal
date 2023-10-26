sys = require('sys')
--

func_req = fail_on_error(sys.func.create({
    namespace_id = '1',
    name = 'func-1'
}))
sys.await_completed(func_req)

namespace_req = fail_on_error(sys.namespace.create({ name = "hamal::name:space::rocks" }))
sys.await_completed(namespace_req)

func_req = fail_on_error(sys.func.create({ namespace_id = namespace_req.id, name = 'func-1' }))
sys.await_completed(func_req)

assert(err == nil)
assert(func_req ~= nil)

_, func = sys.func.get(func_req.id)
assert(func.namespace.id == namespace_req.id)
assert(func.namespace.name == "hamal::name:space::rocks")

err, funcs = sys.func.list()
print(#funcs)
assert(#funcs == 2)