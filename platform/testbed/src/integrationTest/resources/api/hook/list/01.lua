sys = require_plugin('sys')

hooks = fail_on_error(sys.hooks.list())
assert(#hooks == 0)

hook_one_req = fail_on_error(sys.hooks.create({ namespace_id = '1'; name = 'hook-1' }))
sys.await_completed(hook_one_req)

assert(hook_one_req ~= nil)
--
_, hooks = sys.hooks.list()
assert(#hooks == 1)

assert(hook_one_req.id == hooks[1].id)
assert(hooks[1].name == 'hook-1')

hook_two = fail_on_error(sys.hooks.create({ namespace_id = '1'; name = 'hook-2' }))
sys.await_completed(hook_two)

_, hooks = sys.hooks.list()
assert(#hooks == 2)
