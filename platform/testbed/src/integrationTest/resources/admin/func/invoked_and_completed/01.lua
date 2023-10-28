sys = require('sys')

req = fail_on_error(sys.func.create({
    namespace_id = '1',
    name = 'test-func',
    inputs = {},
    code = [[print('invoked')]]
}))
sys.await_completed(req)

invocation_req = fail_on_error(sys.func.invoke({
    id = req.func_id,
    correlation_id = nil,
    inputs = { }
}))
sys.await_completed(invocation_req)

assert(invocation_req.id ~= nil)
assert(invocation_req.status == 'Submitted')
assert(invocation_req.exec_id ~= nil)