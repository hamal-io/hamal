sys = require('sys')

func = fail_on_error(sys.func.create({
    namespace_id = '1',
    name = 'test-func',
    inputs = {},
    code = [[ doomed to fail ]]
}))
sys.await_completed(func)

err, invocation_req = sys.func.invoke(func.id, {
    correlation_id = nil,
    inputs = { }
})
sys.await_completed(invocation_req)

assert(invocation_req.id ~= nil)
assert(invocation_req.status == 'Submitted')

