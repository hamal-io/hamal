sys = require('std.sys').create({
    base_url = context.env.api_host
})

err, hooks = sys.func.list()
assert(err == nil)
assert(#hooks == 0)

err, hook_req = sys.func.create({ namespace_id = '539', name = 'func-1' })
assert(err == nil)
assert(hook_req ~= nil)

_, hooks = sys.func.list()
assert(#hooks == 1)

assert(hook_req.id == hooks[1].id)
assert(hooks[1].name == 'func-1')

func_two = fail_on_error(sys.func.create({ namespace_id = '539', name = 'func-2' }))
_, hooks = sys.func.list()
assert(#hooks == 2)
