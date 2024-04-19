sys = require_plugin('std.sys')

func = fail_on_error(sys.funcs.create({ name = 'test-func-t'; inputs = {}; code = [[4 + 2]] }))
sys.await_completed(func)

endpoint_req = fail_on_error(sys.endpoints.create({ name = "some-amazing-endpoint-t"; func_id = func.id }))
sys.await(endpoint_req)

req_one = fail_on_error(sys.triggers.create_endpoint({
    func_id = func.id,
    name = 'trigger-one',
    inputs = { },
    endpoint_id = endpoint_req.id
}))
sys.await_completed(req_one)

req_two = fail_on_error(sys.triggers.create_endpoint({
    func_id = func.id,
    name = 'trigger-two',
    inputs = { },
    endpoint_id = endpoint_req.id,
}))
sys.await_failed(req_two)

triggers = fail_on_error(sys.triggers.list())
assert(#triggers == 1)