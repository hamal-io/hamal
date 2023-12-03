sys = require_plugin('sys')

local create_req = fail_on_error(sys.funcs.create({
    name = 'created-name',
    inputs = { },
    code = 'created-code'
}))
sys.await_completed(create_req)

update_req = fail_on_error(sys.funcs.update({
    id = create_req.func_id
}))
sys.await_completed(update_req)

func_one = fail_on_error(sys.funcs.get(create_req.func_id))
assert(func_one.name == 'created-name')
assert(func_one.code.version == 1)
assert(func_one.code.value == 'created-code')
assert(func_one.deployment.version == 1)
assert(func_one.deployment.value == 'created-code')