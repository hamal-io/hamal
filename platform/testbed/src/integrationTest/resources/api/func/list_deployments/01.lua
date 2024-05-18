sys = require('std.sys').create({
    base_url = context.env.api_host
})

local func_req = fail_on_error(sys.func.create({
    name = 'test-func',
    inputs = { },
    code = [[code-1]]
}))

sys.await_completed(func_req)

for i = 2, 10 do
    update_req = fail_on_error(sys.func.update({
        id = func_req.id,
        name = 'func-' .. i,
        inputs = { },
        code = [[code-]] .. i
    }))
    sys.await_completed(update_req)

    deploy_req = fail_on_error(sys.func.deploy_latest({
        id = func_req.id,
        message = 'message-' .. i
    }))
    sys.await_completed(deploy_req)
end

func_one = fail_on_error(sys.func.get(func_req.id))

deployments = fail_on_error(sys.func.list_deployments(func_one))
assert(#deployments == 9) --first is missing
assert(deployments[5].message == 'message-6')
assert(deployments[5].version == 6)