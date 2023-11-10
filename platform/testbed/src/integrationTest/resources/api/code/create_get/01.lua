sys = require('sys')

local create_req = fail_on_error(sys.funcs.create({
    flow_id = '1',
    name = 'test-func',
    inputs = {},
    code = [[4 + 2]]
}))
sys.await_completed(create_req)

local func = fail_on_error(sys.funcs.get(create_req.func_id))
local code = fail_on_error(sys.codes.get(func.code.id))

assert(code.id == func.code.id)
assert(code.code == [[4 + 2]])
assert(code.version == 1)