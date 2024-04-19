sys = require_plugin('std.sys')

local func_req = fail_on_error(sys.funcs.create({
    name = 'created-name',
    inputs = {},
    code = 'created-code'
}))
sys.await_completed(func_req)

for i = 2, 20 do
    update_req = fail_on_error(sys.funcs.update({
        id = func_req.id,
        name = 'func-' .. i,
        inputs = { },
        code = [[code-]] .. i
    }))
end
sys.await_completed(update_req)

err, res = sys.funcs.deploy({
    id = '23',
    version = 1
})
assert(err.message == 'Func not found')
assert(res == nil)

err, res = sys.funcs.deploy_latest('23')
assert(err.message == 'Func not found')
assert(res == nil)