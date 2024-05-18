sys = require('std.sys').create({
    base_url = context.env.api_host
})

hooks = fail_on_error(sys.func.list())
assert(#hooks == 0)

hook_req = fail_on_error(sys.func.create({ namespace_id = '539', name = 'func-1' }))
sys.await_completed(hook_req)
assert(hook_req ~= nil)

hooks = fail_on_error(sys.func.list())
assert(#hooks == 1)

assert(hook_req.id == hooks[1].id)
assert(hooks[1].name == 'func-1')

func_two = fail_on_error(sys.func.create({
    namespace_id = '539',
    name = 'func-2'
}))
sys.await_completed(func_two)

hooks = fail_on_error(sys.func.list())
assert(#hooks == 2)
