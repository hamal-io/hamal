sys = require('sys')

local create_req = fail_on_error(sys.func.create({
    name = 'created-name',
    inputs = { },
    code = 'created-code'
}))
sys.await_completed(create_req)

update_req = fail_on_error(sys.func.update({
    id = create_req.func_id,
    name = 'updated-name',
    code = 'updated-code'
}))
sys.await_completed(update_req)

func = fail_on_error(sys.func.get(create_req.func_id))
assert(func.name == 'updated-name')
assert(func.code.current.version == 2)
assert(func.code.current.value == 'updated-code')
assert(func.code.deployed.version == 1)
assert(func.code.deployed.value == 'created-code')