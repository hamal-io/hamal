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
end
sys.await_completed(update_req)

func_one = fail_on_error(sys.func.get(func_req.id))
assert(func_one.code.version == 20)
assert(func_one.code.value == 'code-20')
assert(func_one.deployment.version == 1)
assert(func_one.deployment.value == 'code-1')

deploy_req = fail_on_error(sys.func.deploy_latest({ id = func_one.id }))
sys.await_completed(deploy_req)

func_one = fail_on_error(sys.func.get(func_one.id))
assert(func_one.code.id ~= nil)
assert(func_one.code.version == 20)
assert(func_one.code.value == [[code-20]])
assert(func_one.deployment.version == 20)
assert(func_one.deployment.value == [[code-20]])