sys = require('sys')

err, create_func_req = sys.func.create({
    name = 'test-func',
    inputs = {},
    code = [[print('invoked')]]
})
sys.await_completed(create_func_req)

err, invocation_req = sys.func.invoke(create_func_req.id, {
    correlation_id = nil,
    inputs = { }
})
sys.await_completed(invocation_req)

assert(invocation_req.id ~= nil)
assert(invocation_req.status == 'Submitted')

err, exec = sys.exec.get(invocation_req.id)

assert(err == nil)
assert(exec ~= nil)

assert(exec.correlation_id == '__default__')

