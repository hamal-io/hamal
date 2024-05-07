sys = require_plugin('std.sys')

local func_req = fail_on_error(sys.funcs.create({
    namespace_id = '539',
    name = 'test-func',
    inputs = {},
    code = [[4 + 2]]
}))
sys.await_completed(func_req)

func_one = fail_on_error(sys.funcs.get(func_req.id))
code = fail_on_error(sys.codes.get(func_one.code.id, 1))

assert(code.id == func_one.code.id)
assert(code.value == [[4 + 2]])
assert(code.version == 1)