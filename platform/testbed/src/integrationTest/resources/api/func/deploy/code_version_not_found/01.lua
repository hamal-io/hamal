sys = require_plugin('std.sys')

local func_req = fail_on_error(sys.funcs.create({
    name = 'test-func',
    inputs = {},
    code = [[4 + 2]]
}))
sys.await_completed(func_req)

update_req = fail_on_error(sys.funcs.update({
    id = func_req.id,
    name = 'func-2',
    inputs = { },
    code = 'code-2'
}))
sys.await_completed(update_req)

err, res = sys.funcs.deploy({
    id = func_req.id,
    version = 24
})
assert(err.message == 'Code not found')
assert(res == nil)
