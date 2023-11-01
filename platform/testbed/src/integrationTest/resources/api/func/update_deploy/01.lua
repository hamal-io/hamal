sys = require('sys')

create_req = fail_on_error(sys.func.create({
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
assert(func.mcode.version == 20)
assert(func.mcode.deployed == 1)

deploy_req = fail_on_error(sys.func.deploy({
    id = func.id,
    version = 10
}))

sys.await_completed(deploy_req)
assert(deploy_req.version == 10)

func = fail_on_error(sys.func.get(func.id))
assert(func.mcode.deployed == 10)







