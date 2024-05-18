sys = require('std.sys').create({
    base_url = context.env.api_host
})

local func_req = fail_on_error(sys.func.create({
    name = 'created-name',
    inputs = { },
    code = 'created-code'
}))
sys.await_completed(func_req)

update_req = fail_on_error(sys.func.update({
    id = func_req.id
}))
sys.await_completed(update_req)

func_one = fail_on_error(sys.func.get(func_req.id))
assert(func_one.name == 'created-name')
assert(func_one.code.version == 1)
assert(func_one.code.value == 'created-code')
assert(func_one.deployment.version == 1)
assert(func_one.deployment.value == 'created-code')