sys = require('sys')

local create_req = fail_on_error(sys.func.create({
    name = 'created-name',
    inputs = {},
    code = 'created-code'
}))
sys.await_completed(create_req)

for i = 2, 20 do
    update_req = fail_on_error(sys.func.update({
        id = create_req.func_id,
        name = 'func-' .. i,
        inputs = { },
        code = [[code-]] .. i
    }))
end
sys.await_completed(update_req)

err, res = sys.func.deploy({
    id = '23',
    version = 1
})
assert(err.message == 'Func not found')
assert(res == nil)

err, res = sys.func.deploy_latest('23')
assert(err.message == 'Func not found')
assert(res == nil)