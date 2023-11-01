sys = require('sys')

req = fail_on_error(sys.func.create({
    name = 'test-func',
    inputs = {},
    code = [[4 + 2]]

}))

sys.await_completed(req)

func = fail_on_error(sys.func.get(req.id))
assert(func.code_deployed == 1)

err1, res1 = sys.func.deploy({
    id = func.id,
    version = 3
})

assert(err1.message == 'Code not found')
assert(err1['message'] == 'Code not found')
assert(res1 == nil)

err2, res2 = sys.func.deploy({
    id = '23',
    version = 3
})

assert(err2.message == 'Func not found')
assert(err2['message'] == 'Func not found')
assert(res2 == nil)


