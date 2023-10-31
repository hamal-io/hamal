sys = require('sys')

err, f1 = sys.func.create({
    namespace_id = '1',
    name = 'test-func',
    inputs = {},
    code = [[4 + 2]]
})

assert(err == nil)
sys.await_completed(f1)

err, func = sys.func.get(f1.func_id)
assert(err == nil)

err, code = sys.code.get(func.code_id)
assert(err == nil)

assert(code.id == func.code_id)
assert(code.code == [[4 + 2]])
assert(code.version == 1)