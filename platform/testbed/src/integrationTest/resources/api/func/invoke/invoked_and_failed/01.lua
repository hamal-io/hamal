sys = require('std.sys').create({
    base_url = context.env.api_host
})

func_req = fail_on_error(sys.func.create({
    namespace_id = '539',
    name = 'test-func',
    inputs = {},
    code = [[ doomed to fail ]]
}))
sys.await_completed(func_req)

err, invocation_req = sys.func.invoke({
    id = func_req.id,
    correlation_id = nil,
    inputs = { }
})
sys.await_completed(invocation_req)

assert(invocation_req.requestId ~= nil)
assert(invocation_req.requestStatus == 'Submitted')
assert(invocation_req.id ~= nil)
