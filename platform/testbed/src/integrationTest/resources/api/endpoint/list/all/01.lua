sys = require_plugin('sys')

endpoints = fail_on_error(sys.endpoints.list())
assert(#endpoints == 0)

func_one = fail_on_error(sys.funcs.create({
    name = 'test-func',
    inputs = {},
    code = [[4 + 2]]
}))
sys.await_completed(func_one)

func_two = fail_on_error(sys.funcs.create({
    name = 'test-func-2',
    inputs = {},
    code = [[4 + 2]]
}))
sys.await_completed(func_two)

endpoint_one_req = fail_on_error(sys.endpoints.create({ name = 'endpoint-1'; func_id = func_one.func_id }))
sys.await_completed(endpoint_one_req)

assert(endpoint_one_req ~= nil)
--
_, endpoints = sys.endpoints.list()
assert(#endpoints == 1)

assert(endpoint_one_req.id == endpoints[1].id)
assert(endpoints[1].name == 'endpoint-1')

endpoint_two = fail_on_error(sys.endpoints.create({ name = 'endpoint-2'; func_id = func_two.func_id }))
sys.await_completed(endpoint_two)

_, endpoints = sys.endpoints.list()
assert(#endpoints == 2)
