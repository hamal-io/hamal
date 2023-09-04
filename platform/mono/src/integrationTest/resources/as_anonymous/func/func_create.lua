sys = require('sys')

--anonymous account can not create a function

err, response = sys.func.create({
    name = 'empty-test-func',
    inputs = {},
    code = [[4 + 2]]
})

assert(err ~= nil)
assert(response == nil)
assert(err.message == 'Account not found')