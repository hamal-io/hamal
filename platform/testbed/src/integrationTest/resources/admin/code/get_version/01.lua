sys = require('sys')

err, f1 = sys.func.create({
    name = 'empty-test-func',
    inputs = {},
    code = [[4 + 2]]
})

assert(err == nil)
sys.await_completed(f1)

err, func = sys.func.get(f1.id)
assert(err == nil)

err, code = sys.code.get_version(func.code_id, 1)
assert(err == nil)

assert(code.id == func.code_id)
assert(code.code == [[4 + 2]])
assert(code.version == 1)