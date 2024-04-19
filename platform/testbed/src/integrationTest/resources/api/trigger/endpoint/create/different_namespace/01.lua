sys = require_plugin('std.sys')

namespace_req = fail_on_error(sys.namespaces.append({ name = "hamal::namespace::rocks" }))
sys.await_completed(namespace_req)

func_req = fail_on_error(sys.funcs.create({ namespace_id = namespace_req.id, name = 'test-func'; inputs = {}; code = [[4 + 2]] }))
sys.await_completed(func_req)

endpoint = fail_on_error(sys.endpoints.create({ namespace_id = namespace_req.id, name = "some-amazing-endpoint"; func_id = func_req.id }))
sys.await(endpoint)

req_one = fail_on_error(sys.triggers.create_endpoint({
    namespace_id = namespace_req.id,
    func_id = func_req.id,
    name = 'endpoint-trigger',
    inputs = { },
    endpoint_id = endpoint.id
}))
sys.await_completed(req_one)

assert(req_one.id ~= nil)
assert(req_one.request_status == 'Submitted')
assert(req_one.id ~= nil)
assert(req_one.workspace_id == '539')
assert(req_one.namespace_id == namespace_req.id)

req_two = fail_on_error(sys.triggers.get(req_one.id))
assert(req_two.type == 'Endpoint')
assert(req_two.name == 'endpoint-trigger')
assert(req_two.func.name == "test-func")
assert(req_two.namespace.id == namespace_req.id)
assert(req_two.namespace.name == "hamal::namespace::rocks")
assert(req_two.endpoint.name == "some-amazing-endpoint")

