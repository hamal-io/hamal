sys = require('sys')

err, hooks = sys.hook.list()
assert(err == nil)
assert(#hooks == 0)

err, hook_one_req = sys.hook.create({ name = 'hook-1' })
sys.await_completed(hook_one_req)

assert(err == nil)
assert(hook_one_req ~= nil)
--
_, hooks = sys.hook.list()
assert(#hooks == 1)

assert(hook_one_req.id == hooks[1].id)
assert(hooks[1].name == 'hook-1')

err, hook_two_req = sys.hook.create({ name = 'hook-2' })
sys.await_completed(hook_two_req)

_, hooks = sys.hook.list()
assert(#hooks == 2)
