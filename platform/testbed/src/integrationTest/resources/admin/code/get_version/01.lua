sys = require('sys')

f1 = fail_on_error(sys.func.create({
    namespace_id = '1',
    name = 'test-func',
    inputs = {},
    code = [[4 + 2]]
}))
sys.await_completed(f1)

func = fail_on_error(sys.func.get(f1.id))

code = fail_on_error(sys.code.get(func.code_id, 1))

assert(code.id == func.code_id)
assert(code.code == [[4 + 2]])
assert(code.version == 1)