sys = require('sys')

local create_req = fail_on_error(sys.func.create({
    namespace_id = '1',
    name = 'test-func',
    inputs = {},
    code = [[4 + 2]]
}))
sys.await_completed(create_req)

local func = fail_on_error(sys.func.get(create_req.func_id))
local code = fail_on_error(sys.code.get(func.code.id))

assert(code.id == func.code.id)
assert(code.code == [[4 + 2]])
assert(code.version == 1)