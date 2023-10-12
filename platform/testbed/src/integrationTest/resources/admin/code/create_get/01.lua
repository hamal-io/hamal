sys = require('sys')

err, create_func_req = sys.func.create({
    name = 'empty-test-func',
    inputs = {},
    code = [[4 + 2]]
})

sys.await_completed(create_func_req)


err, xcode_id = sys.func.get(create_func_req.code_id)
assert(err == nil)

err, xcode = sys.code.get(code_get(xcode_id))
assert(err == nil)

assert(xcode.id == create_func_req.code_id)
assert(xcode.code == [[4 + 2]])
assert(xcode.version == create_func_req.code_version)
