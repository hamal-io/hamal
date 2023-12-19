sys = require_plugin('sys')

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
    sys.await_completed(update_req)

    deploy_req = fail_on_error(sys.funcs.deploy_latest(create_req.func_id, 'message-' .. i))
    sys.await_completed(deploy_req)
end

func_one = fail_on_error(sys.funcs.get(create_req.func_id))
deployments = fail_on_error(sys.funcs.list_deployments({ id = create_req.func_id }))
assert(#deployments == 19) --first is missing
assert(deployments[10].message == 'message-11')
assert(deployments[10].version == 11)