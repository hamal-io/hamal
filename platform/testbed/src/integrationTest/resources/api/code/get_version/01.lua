sys = require('sys')

local create_req = fail_on_error(sys.func.create({
    flow_id = '1',
    name = 'test-func',
    inputs = {},
    code = [[4 + 2]]
}))
sys.await_completed(create_req)

func = fail_on_error(sys.func.get(create_req.func_id))
code = fail_on_error(sys.code.get(func.code.id, 1))

assert(code.id == func.code.id)
assert(code.code == [[4 + 2]])
assert(code.version == 1)