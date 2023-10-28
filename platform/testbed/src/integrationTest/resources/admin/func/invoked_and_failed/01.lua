sys = require('sys')

func_req = fail_on_error(sys.func.create({
    namespace_id = '1',
    name = 'test-func',
    inputs = {},
    code = [[ doomed to fail ]]
}))
sys.await_completed(func_req)

err, invocation_req = sys.func.invoke({
    id = func_req.func_id,
    correlation_id = nil,
    inputs = { }
})
sys.await_completed(invocation_req)

assert(invocation_req.id ~= nil)
assert(invocation_req.status == 'Submitted')
assert(invocation_req.exec_id ~= nil)

