sys = require('sys')

local create_req = fail_on_error(sys.func.create({
    name = 'test-func',
    inputs = {},
    code = [[4 + 2]]
}))
sys.await_completed(create_req)

update_req = fail_on_error(sys.func.update({
    id = create_req.func_id,
    name = 'func-2',
    inputs = { },
    code = 'code-2'
}))
sys.await_completed(update_req)

err, res = sys.func.deploy({
    id = create_req.func_id,
    version = 24
})
assert(err.message == 'Code not found')
assert(res == nil)