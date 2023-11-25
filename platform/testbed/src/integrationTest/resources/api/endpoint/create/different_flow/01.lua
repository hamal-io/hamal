sys = require('sys')
--
flow = fail_on_error(sys.flows.create({ name = "hamal::flow::rocks" }))
sys.await_completed(flow)

func_one = fail_on_error(sys.funcs.create({
    name = 'test-func',
    inputs = {},
    code = [[4 + 2]],
    flow_id = flow.flow_id
}))
sys.await_completed(func_one)

endpoint = fail_on_error(sys.endpoints.create({
    flow_id = flow.id,
    func_id = func_one.func_id,
    name = 'test-endpoint'
}))
sys.await_completed(endpoint)

assert(endpoint ~= nil)
assert(endpoint.id ~= nil)
assert(endpoint.status == 'Submitted')
assert(endpoint.endpoint_id ~= nil)
assert(endpoint.group_id == '1')
assert(endpoint.func_id == func_one.func_id)

_, endpoint = sys.endpoints.get(endpoint.id)
assert(endpoint.func.id == func_one.id)
assert(endpoint.func.name == "test-func")

err, endpoints = sys.endpoints.list()
assert(#endpoints == 1)