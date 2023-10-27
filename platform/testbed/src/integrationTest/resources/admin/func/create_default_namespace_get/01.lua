sys = require('sys')

err, func_req = sys.func.create({
    namespace_id = '1',
    name = 'empty-test-func',
    inputs = {},
    code = [[4 + 2]]
})
assert(err == nil)
sys.await_completed(func_req)

assert(func_req.req_id ~= nil)
assert(func_req.status == 'Submitted')
assert(func_req.id ~= nil)

err, func = sys.func.get(func_req.id)
assert(err == nil)

assert(func.id == func_req.id)
assert(func.namespace.id == '1')
assert(func.namespace.name == 'hamal')
assert(func.name == 'empty-test-func')
assert(func.code == [[4 + 2]])
assert(func.code_id ~= nil)
assert(func.code_version == 1)