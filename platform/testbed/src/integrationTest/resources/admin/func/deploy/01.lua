sys = require('sys')

req = fail_on_error(sys.func.create({
    name = 'test-func',
    inputs = {},
    code = [[4 + 2]]
}))

sys.await_completed(req)

func = fail_on_error(sys.func.get(req.id))
assert(func.code_deployed == 1)

res = fail_on_error(sys.func.deploy({
    id = func.id,
    version = 3
}))

func = fail_on_error(sys.func.get(req.id))
assert(func.code_deployed == 3)

