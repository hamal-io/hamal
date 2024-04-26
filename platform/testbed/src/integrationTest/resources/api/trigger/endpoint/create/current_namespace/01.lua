sys = require_plugin('std.sys')

func_req = fail_on_error(sys.funcs.create({ name = 'test-func'; inputs = {}; code = [[4 + 2]] }))
sys.await_completed(func_req)

req_one = fail_on_error(sys.triggers.create_endpoint({
    func_id = func_req.id,
    name = 'endpoint-trigger',
    inputs = { }
}))
sys.await_completed(req_one)

assert(req_one.request_id ~= nil)
assert(req_one.request_status == 'Submitted')
assert(req_one.id ~= nil)
assert(req_one.workspace_id == '539')
assert(req_one.namespace_id == '539')

req_two = fail_on_error(sys.triggers.get(req_one.id))

assert(req_two.type == 'Endpoint')
assert(req_two.name == 'endpoint-trigger')
assert(req_two.func.name == "test-func")
assert(req_two.namespace.id == '539')
assert(req_two.namespace.name == "root-namespace")

