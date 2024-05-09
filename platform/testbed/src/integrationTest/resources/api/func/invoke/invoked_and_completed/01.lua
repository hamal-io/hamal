sys = require('std.sys').create({
    base_url = context.env.api_host
})

func = fail_on_error(sys.func.create({
    namespace_id = '539',
    name = 'test-func',
    inputs = {},
    code = [[print('invoked')]]
}))
--sys.await_completed(func)

invocation_req = fail_on_error(sys.func.invoke({
    id = func.id,
    correlation_id = nil,
    inputs = { }
}))
--sys.await_completed(invocation_req)

--FIXME-341 snakecase
--assert(invocation_req.request_id ~= nil)
--assert(invocation_req.request_status == 'Submitted')
assert(invocation_req.id ~= nil)