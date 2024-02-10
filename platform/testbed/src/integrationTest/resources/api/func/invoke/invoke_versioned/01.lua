sys = require_plugin('sys')

req = fail_on_error(sys.funcs.create({
    namespace_id = '1',
    name = 'test-func3',
    inputs = {},
    code = [[print('invoked')]]
}))
sys.await_completed(req)

for i = 2, 10 do
    update_req = fail_on_error(sys.funcs.update({
        id = req.func_id,
        name = 'func3-' .. i,
        inputs = { },
        code = [[code3-]] .. i
    }))
    sys.await_completed(update_req)
end
func_one = fail_on_error(sys.funcs.get(req.func_id))
assert(func_one.code.version == 10)

invocation_req = fail_on_error(sys.funcs.invoke({
    id = req.func_id,
    correlation_id = nil,
    inputs = { },
    version = 5
}))
sys.await_completed(invocation_req)

assert(invocation_req.id ~= nil)
assert(invocation_req.status == 'Submitted')
assert(invocation_req.exec_id ~= nil)
