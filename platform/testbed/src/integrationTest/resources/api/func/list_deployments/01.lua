sys = require('std.sys').create({
    base_url = context.env.api_host
})

local func_req = fail_on_error(sys.func.create({
    name = 'test-func',
    inputs = { },
    code = [[code-1]]
}))

sys.await_completed(func_req)

for i = 2, 20 do
    update_req = fail_on_error(sys.func.update({
        id = func_req.id,
        name = 'func-' .. i,
        inputs = { },
        code = [[code-]] .. i
    }))
    sys.await_completed(update_req)

    deploy_req = fail_on_error(sys.func.deploy_latest(func_req.id, 'message-' .. i))
    sys.await_completed(deploy_req)
end

func_one = fail_on_error(sys.func.get(func_req.id))

deployments = fail_on_error(sys.func.list_deployments(func_one))
assert(#deployments == 19) --first is missing
assert(deployments[10].message == 'message-11')
assert(deployments[10].version == 11)