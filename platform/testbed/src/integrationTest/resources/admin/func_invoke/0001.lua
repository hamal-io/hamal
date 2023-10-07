admin = require('sysadmin')

err, create_func_req = admin.func.create({
    name = 'test-func',
    inputs = {},
    code = [[print('invoked')]]
})
assert(err == nil)
admin.await_completed(create_func_req)

err, invocation_req = admin.func.invoke(create_func_req.id, {
    correlation_id = nil,
    inputs = { }
})
admin.await_completed(invocation_req)

assert(invocation_req.id ~= nil)
assert(invocation_req.status == 'Submitted')

err, exec = admin.exec.get(invocation_req.id)

assert(err == nil)
assert(exec ~= nil)

assert(exec.correlation_id == '__default__')

