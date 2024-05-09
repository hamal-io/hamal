sys = require('std.sys').create({
    base_url = context.env.api_host
})

func_req = fail_on_error(sys.func.create({
    namespace_id = '539',
    name = 'test-func3',
    inputs = {},
    code = [[print('invoked')]]
}))
--sys.await_completed(func_req)

for i = 2, 10 do
    update_req = fail_on_error(sys.func.update({
        id = func_req.id,
        name = 'func3-' .. i,
        inputs = { },
        code = [['code3-']] .. i
    }))
    --sys.await_completed(update_req)
end

func_one = fail_on_error(sys.func.get(func_req.id))
assert(func_one.code.version == 10)

invocation_req = fail_on_error(sys.func.invoke({
    id = func_req.id,
    correlation_id = nil,
    inputs = { },
    version = 5
}))

--FIXME-341 snakecase
--sys.await_completed(invocation_req)
--assert(invocation_req.request_id ~= nil)
--assert(invocation_req.request_status == 'Submitted')
assert(invocation_req.id ~= nil)
