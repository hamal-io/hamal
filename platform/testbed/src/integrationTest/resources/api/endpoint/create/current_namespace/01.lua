sys = require_plugin('sys')

func_one = fail_on_error(sys.funcs.create({
    name = 'test-func',
    inputs = {},
    code = [[4 + 2]]
}))
sys.await_completed(func_one)

submitted_endpoint = fail_on_error(sys.endpoints.create({
    func_id = func_one.func_id,
    name = 'test-endpoint'
}))

sys.await_completed(submitted_endpoint)

assert(submitted_endpoint.id ~= nil)
assert(submitted_endpoint.status == 'Submitted')
assert(submitted_endpoint.endpoint_id ~= nil)
assert(submitted_endpoint.workspace_id == '1')
assert(submitted_endpoint.func_id == func_one.func_id)

endpoint = fail_on_error(sys.endpoints.get(submitted_endpoint.endpoint_id))
assert(endpoint.id == submitted_endpoint.endpoint_id)
assert(endpoint.func.name == 'test-func')
assert(endpoint.name == 'test-endpoint')