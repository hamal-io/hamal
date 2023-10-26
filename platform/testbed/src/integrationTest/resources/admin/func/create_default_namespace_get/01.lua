sys = require('sys')

err, create_func_req = sys.func.create({
    namespace_id = '1',
    name = 'empty-test-func',
    inputs = {},
    code = [[4 + 2]]
})
assert(err == nil)
sys.await_completed(create_func_req)

assert(create_func_req.req_id ~= nil)
assert(create_func_req.status == 'Submitted')
assert(create_func_req.id ~= nil)

err, func = sys.func.get(create_func_req.id)
assert(err == nil)

assert(func.id == create_func_req.id)
assert(func.namespace.id == '1')
assert(func.namespace.name == 'hamal')
assert(func.name == 'empty-test-func')
assert(func.code == [[4 + 2]])
assert(func.code_id ~= nil)
assert(func.code_version == 1)