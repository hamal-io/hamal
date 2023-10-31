sys = require('sys')
--
namespace = fail_on_error(sys.namespace.create({ name = "hamal::name:space::rocks" }))
sys.await_completed(namespace)

func = fail_on_error(sys.func.create({
    namespace_id = namespace.id,
    name = 'func-1'
}))
sys.await_completed(func)

assert(func ~= nil)
assert(func.group_id == '1')
assert(func.namespace_id == namespace.id)

func = fail_on_error(sys.func.get(func.id))
assert(func.namespace.id == namespace.id)
assert(func.namespace.name == "hamal::name:space::rocks")

err, hooks = sys.func.list()
assert(#hooks == 1)