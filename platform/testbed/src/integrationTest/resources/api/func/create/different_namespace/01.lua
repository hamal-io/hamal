sys = require('std.sys').create({
    base_url = context.env.api_host
})

namespace = fail_on_error(sys.namespace.append({ name = "hamal::namespace::rocks" }))
sys.await_completed(namespace)

func_one = fail_on_error(sys.func.create({
    namespace_id = namespace.id,
    name = 'func-1'
}))
sys.await_completed(func_one)

assert(func_one ~= nil)
assert(func_one.workspaceId == '539')
assert(func_one.namespaceId == namespace.id)

func_one = fail_on_error(sys.func.get(func_one.id))
print(dump(func_one))
assert(func_one.namespace.id == namespace.id)
assert(func_one.namespace.name == "hamal::namespace::rocks")

err, hooks = sys.func.list()
assert(#hooks == 1)