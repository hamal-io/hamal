sys = require_plugin('std.sys')

func = fail_on_error(sys.funcs.create({
    namespace_id = '539',
    name = 'test-func',
    inputs = {},
    code = [[print('invoked')]]
}))
sys.await_completed(func)

invocation_req = fail_on_error(sys.funcs.invoke({
    id = func.id,
    correlation_id = nil,
    inputs = { }
}))
sys.await_completed(invocation_req)

assert(invocation_req.request_id ~= nil)
assert(invocation_req.request_status == 'Submitted')
assert(invocation_req.id ~= nil)