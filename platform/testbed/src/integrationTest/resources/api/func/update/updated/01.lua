sys = require_plugin('sys')

local create_req = fail_on_error(sys.funcs.create({
    name = 'created-name',
    inputs = { },
    code = 'created-code'
}))
sys.await_completed(create_req)

update_req = fail_on_error(sys.funcs.update({
    id = create_req.func_id,
    name = 'updated-name',
    code = 'updated-code'
}))
sys.await_completed(update_req)

func_one = fail_on_error(sys.funcs.get(create_req.func_id))
assert(func_one.name == 'updated-name')
assert(func_one.code.current.version == 2)
assert(func_one.code.current.value == 'updated-code')
assert(func_one.code.deployed.version == 1)
assert(func_one.code.deployed.value == 'created-code')