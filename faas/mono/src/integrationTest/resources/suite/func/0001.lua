local test = require('test')
local sys = require('sys')

local err, func_res = sys.func.create({
    name = 'empty-test-func',
    inputs = {},
    code = [[4 + 2]]
})

assert(err == nil)

assert(func_res.req_id ~= nil)
assert(func_res.status == 'Submitted')
assert(func_res.id ~= nil)

local err, func = sys.func.get(func_res.id)
assert(err == nil)

assert(func.id == func_res.id)
assert(func.name == 'empty-test-func')
assert(func.code == [[4 + 2]])

test.complete()