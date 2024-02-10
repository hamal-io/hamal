sys = require_plugin('sys')

local create_req = fail_on_error(sys.funcs.create({
    namespace_id = '1',
    name = 'test-func',
    inputs = {},
    code = [[4 + 2]]
}))
sys.await_completed(create_req)

func_one = fail_on_error(sys.funcs.get(create_req.func_id))
code = fail_on_error(sys.codes.get(func_one.code.id, 1))

assert(code.id == func_one.code.id)
assert(code.code == [[4 + 2]])
assert(code.version == 1)