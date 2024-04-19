sys = require_plugin('std.sys')
--
namespace = fail_on_error(sys.namespaces.append({ name = "hamal::namespace::rocks" }))
sys.await_completed(namespace)

func_one = fail_on_error(sys.funcs.create({
    namespace_id = namespace.id,
    name = 'func-1'
}))
sys.await_completed(func_one)

assert(func_one ~= nil)
assert(func_one.workspace_id == '539')
assert(func_one.namespace_id == namespace.id)

func_one = fail_on_error(sys.funcs.get(func_one.id))
assert(func_one.namespace.id == namespace.id)
assert(func_one.namespace.name == "hamal::namespace::rocks")

err, hooks = sys.funcs.list()
assert(#hooks == 1)