sys = require('sys')

create_func_req = fail_on_error(sys.func.create({
    namespace_id = '1',
    name = 'test-func',
    inputs = {},
    code = [[print('invoked')]]
}))
sys.await_completed(create_func_req)

err, invocation_req = sys.func.invoke(create_func_req.id, {
    correlation_id = nil,
    inputs = { }
})
sys.await_completed(invocation_req)

assert(invocation_req.id ~= nil)
assert(invocation_req.status == 'Submitted')

