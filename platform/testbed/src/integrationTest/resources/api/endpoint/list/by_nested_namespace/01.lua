sys = require_plugin('std.sys')

namespace_one_req = fail_on_error(sys.namespaces.append({ name = 'namespace-one' }))
sys.await_completed(namespace_one_req)
func_one = fail_on_error(sys.funcs.create({
    namespace_id = namespace_one_req.id,
    name = 'test-func',
    inputs = {},
    code = [[4 + 2]],
}))
sys.await_completed(func_one)
sys.await_completed(fail_on_error(sys.endpoints.create({ namespace_id = namespace_one_req.id, func_id = func_one.id, name = 'endpoint-1' })))

namespace_two_req = fail_on_error(sys.namespaces.append({ name = 'namespace-two' }))
sys.await_completed(namespace_two_req)
func_two = fail_on_error(sys.funcs.create({
    namespace_id = namespace_two_req.id,
    name = 'test-func-2',
    inputs = {},
    code = [[4 + 2]]
}))
sys.await_completed(func_two)
sys.await_completed(fail_on_error(sys.endpoints.create({ namespace_id = namespace_two_req.id, func_id = func_two.id, name = 'endpoint-2' })))

assert(#fail_on_error(sys.endpoints.list({ namespace_ids = { namespace_one_req.id } })) == 1)
assert(#fail_on_error(sys.endpoints.list({ namespace_ids = { namespace_two_req.id } })) == 1)
--

root_namespace_endpoints = fail_on_error(sys.endpoints.list({ namespace_ids = { '539' } }))
assert(#root_namespace_endpoints == 2)
assert(root_namespace_endpoints[1].name == 'endpoint-2')
assert(root_namespace_endpoints[2].name == 'endpoint-1')
