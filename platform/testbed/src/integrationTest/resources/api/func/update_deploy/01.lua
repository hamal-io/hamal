sys = require('sys')

local create_req = fail_on_error(sys.func.create({
    name = 'test-func',
    inputs = { },
    code = [[4 + 2]]
}))

sys.await_completed(create_req)

for i = 1, 19 do
    update_req = fail_on_error(sys.func.update({
        id = create_req.func_id,
        name = 'func-' .. i,
        inputs = { },
        code = [[code-]] .. i
    }))
    sys.await_completed(update_req)
end

func = fail_on_error(sys.func.get(create_req.func_id))
assert(func.code.version == 20)
assert(func.code.deployed_version == 1)

deploy_req = fail_on_error(sys.func.deploy({
    id = func.id,
    version = 20
}))

sys.await_completed(deploy_req)
assert(deploy_req.deployed_version == 20)

func = fail_on_error(sys.func.get(func.id))
assert(func.code.deployed_version == 20)

deploy_req = fail_on_error(sys.func.deploy({
    id = func.id,
    version = 5
}))

sys.await_completed(deploy_req)

func = fail_on_error(sys.func.get(func.id))
assert(func.code.deployed_version == 5)

deploy_latest_req = fail_on_error(sys.func.deploy_latest(func.id))
sys.await_completed(deploy_latest_req)

func = fail_on_error(sys.func.get(func.id))
assert(func.code.deployed_version == 20)