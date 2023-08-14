local sys = require('sys')

local err, create_func_req = sys.func.create({
    name = 'empty-test-func',
    inputs = {},
    code = [[4 + 2]]
})

sys.await(create_func_req)

assert(err == nil)

assert(create_func_req.req_id ~= nil)
assert(create_func_req.status == 'Submitted')
assert(create_func_req.id ~= nil)

local err, func = sys.func.get(create_func_req.id)
assert(err == nil)

assert(func.id == create_func_req.id)
assert(func.name == 'empty-test-func')
assert(func.code == [[4 + 2]])