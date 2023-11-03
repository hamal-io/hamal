sys = require('sys')

local create_req = fail_on_error(sys.func.create({
    name = 'test-func',
    inputs = { },
    code = [[4 + 2]]
}))

sys.await_completed(create_req)

update_req = fail_on_error(sys.func.update({
    id = create_req.func_id
}))

sys.await_completed(update_req)
func = fail_on_error(sys.func.get(create_req.func_id))

assert(func.name == 'test-func')
assert(func.code.value == [[4 + 2]])