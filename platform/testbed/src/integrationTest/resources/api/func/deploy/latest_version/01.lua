sys = require('sys')

local create_req = fail_on_error(sys.func.create({
    name = 'test-func',
    inputs = { },
    code = [[code-1]]
}))

sys.await_completed(create_req)

for i = 2, 20 do
    update_req = fail_on_error(sys.func.update({
        id = create_req.func_id,
        name = 'func-' .. i,
        inputs = { },
        code = [[code-]] .. i
    }))
end
sys.await_completed(update_req)

func = fail_on_error(sys.func.get(create_req.func_id))
assert(func.code.current.version == 20)
assert(func.code.current.value == 'code-20')
assert(func.code.deployed.version == 1)
assert(func.code.deployed.value == 'code-1')

deploy_req = fail_on_error(sys.func.deploy_latest(func.id))
sys.await_completed(deploy_req)

func = fail_on_error(sys.func.get(func.id))
assert(func.code.id ~= nil)
assert(func.code.current.version == 20)
assert(func.code.current.value == [[code-20]])
assert(func.code.deployed.version == 20)
assert(func.code.deployed.value == [[code-20]])
