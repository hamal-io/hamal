sys = require('sys')

local create_req = fail_on_error(sys.func.create({
    name = 'test-func',
    inputs = {},
    code = [[4 + 2]]

}))

sys.await_completed(create_req)

err, res = sys.func.deploy({
    id = create_req.func_id,
    version = 3
})
assert(err.message == 'Code not found')
assert(res == nil)

err, res = sys.func.deploy({
    id = '23',
    version = 1
})
assert(err.message == 'Func not found')
assert(res == nil)