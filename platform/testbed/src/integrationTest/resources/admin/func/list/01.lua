sys = require('sys')

err, hooks = sys.func.list()
assert(err == nil)
assert(#hooks == 0)

err, hook_one_req = sys.func.create({ name = 'func-1' })
sys.await_completed(hook_one_req)

assert(err == nil)
assert(hook_one_req ~= nil)
--
_, hooks = sys.func.list()
assert(#hooks == 1)

assert(hook_one_req.id == hooks[1].id)
assert(hooks[1].name == 'func-1')

err, hook_two_req = sys.func.create({ name = 'func-2' })
sys.await_completed(hook_two_req)

_, hooks = sys.func.list()
assert(#hooks == 2)