sys = require('std.sys').create({
    base_url = context.env.api_host
})

local func_req = fail_on_error(sys.func.create({
    name = 'test-func',
    inputs = {},
    code = [[4 + 2]]
}))
--sys.await_completed(func_req)

update_req = fail_on_error(sys.func.update({
    id = func_req.id,
    name = 'func-2',
    inputs = { },
    code = 'code-2'
}))
--sys.await_completed(update_req)

err, res = sys.func.deploy({
    id = func_req.id,
    version = 24
})

assert(err.message == 'Code not found')
assert(res == nil)
