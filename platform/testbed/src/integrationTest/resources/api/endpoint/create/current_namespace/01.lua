sys = require_plugin('sys')

func_one = fail_on_error(sys.funcs.create({
    name = 'test-func',
    inputs = {},
    code = [[4 + 2]]
}))
sys.await_completed(func_one)

requested_endpoint = fail_on_error(sys.endpoints.create({
    func_id = func_one.id,
    name = 'test-endpoint'
}))

sys.await_completed(requested_endpoint)

assert(requested_endpoint.request_id ~= nil)
assert(requested_endpoint.request_status == 'Submitted')
assert(requested_endpoint.id ~= nil)
assert(requested_endpoint.workspace_id == '539')
assert(requested_endpoint.func_id == func_one.id)

endpoint = fail_on_error(sys.endpoints.get(requested_endpoint.id))
assert(endpoint.id == requested_endpoint.id)
assert(endpoint.func.name == 'test-func')
assert(endpoint.name == 'test-endpoint')