sys = require_plugin('sys')
--
namespace = fail_on_error(sys.namespaces.append({ name = "hamal::namespace::rocks" }))
sys.await_completed(namespace)

func_one = fail_on_error(sys.funcs.create({
    name = 'test-func',
    inputs = {},
    code = [[4 + 2]],
    namespace_id = namespace.namespace_id
}))
sys.await_completed(func_one)

endpoint = fail_on_error(sys.endpoints.create({
    namespace_id = namespace.id,
    func_id = func_one.func_id,
    name = 'test-endpoint'
}))
sys.await_completed(endpoint)

assert(endpoint ~= nil)
assert(endpoint.id ~= nil)
assert(endpoint.status == 'Submitted')
assert(endpoint.endpoint_id ~= nil)
assert(endpoint.workspace_id == '1')
assert(endpoint.func_id == func_one.func_id)

_, endpoint = sys.endpoints.get(endpoint.id)
assert(endpoint.func.id == func_one.id)
assert(endpoint.func.name == "test-func")

err, endpoints = sys.endpoints.list()
assert(#endpoints == 1)