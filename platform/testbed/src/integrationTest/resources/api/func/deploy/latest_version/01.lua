sys = require('sys')

local create_req = fail_on_error(sys.funcs.create({
    name = 'test-func',
    inputs = { },
    code = [[code-1]]
}))

sys.await_completed(create_req)

for i = 2, 20 do
    update_req = fail_on_error(sys.funcs.update({
        id = create_req.func_id,
        name = 'func-' .. i,
        inputs = { },
        code = [[code-]] .. i
    }))
end
sys.await_completed(update_req)

func_one = fail_on_error(sys.funcs.get(create_req.func_id))
assert(func_one.code.current.version == 20)
assert(func_one.code.current.value == 'code-20')
assert(func_one.code.deployed.version == 1)
assert(func_one.code.deployed.value == 'code-1')

deploy_req = fail_on_error(sys.funcs.deploy_latest(func_one.id))
sys.await_completed(deploy_req)

func_one = fail_on_error(sys.funcs.get(func_one.id))
assert(func_one.code.id ~= nil)
assert(func_one.code.current.version == 20)
assert(func_one.code.current.value == [[code-20]])
assert(func_one.code.deployed.version == 20)
assert(func_one.code.deployed.value == [[code-20]])
