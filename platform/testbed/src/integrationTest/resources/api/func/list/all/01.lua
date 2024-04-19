sys = require_plugin('std.sys')

err, hooks = sys.funcs.list()
assert(err == nil)
assert(#hooks == 0)

hook_req = fail_on_error(sys.funcs.create({ namespace_id = '539', name = 'func-1' }))
sys.await_completed(hook_req)

assert(err == nil)
assert(hook_req ~= nil)
--
_, hooks = sys.funcs.list()
assert(#hooks == 1)

assert(hook_req.id == hooks[1].id)
assert(hooks[1].name == 'func-1')

func_two = fail_on_error(sys.funcs.create({ namespace_id = '539', name = 'func-2' }))
sys.await_completed(func_two)

_, hooks = sys.funcs.list()
assert(#hooks == 2)
