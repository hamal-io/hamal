sys = require_plugin('std.sys')

func_req = fail_on_error(sys.funcs.create({
    namespace_id = '539',
    name = 'test-func',
    inputs = {},
    code = [[ doomed to fail ]]
}))
--sys.await_completed(func_req)

err, invocation_req = sys.funcs.invoke({
    id = func_req.id,
    correlation_id = nil,
    inputs = { }
})
--sys.await_completed(invocation_req)

assert(invocation_req.request_id ~= nil)
assert(invocation_req.request_status == 'Submitted')
assert(invocation_req.id ~= nil)

