admin = require('sys')

err, create_func_req = admin.func.create({
    name = 'empty-test-func',
    inputs = {},
    code = [[4 + 2]]
})

admin.await_completed(create_func_req)

assert(err == nil)

assert(create_func_req.req_id ~= nil)
assert(create_func_req.status == 'Submitted')
assert(create_func_req.id ~= nil)

err, func = admin.func.get(create_func_req.id)
assert(err == nil)

assert(func.id == create_func_req.id)
assert(func.name == 'empty-test-func')
assert(func.code == [[4 + 2]])