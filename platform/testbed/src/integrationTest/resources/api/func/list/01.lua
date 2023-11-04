sys = require('sys')

err, hooks = sys.func.list()
assert(err == nil)
assert(#hooks == 0)

hook = fail_on_error(sys.func.create({ namespace_id = '1', name = 'func-1' }))
sys.await_completed(hook)

assert(err == nil)
assert(hook ~= nil)
--
_, hooks = sys.func.list()
assert(#hooks == 1)

assert(hook.id == hooks[1].id)
assert(hooks[1].name == 'func-1')

func_two = fail_on_error(sys.func.create({ namespace_id = '1', name = 'func-2' }))
sys.await_completed(func_two)

_, hooks = sys.func.list()
assert(#hooks == 2)