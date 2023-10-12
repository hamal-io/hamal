sys = require('sys')

_, namespace_req = sys.namespace.create({ name = 'namespace-1' })

-- hook name is unique
err, hook_req = sys.hook.create({ name = 'hook-name' })
sys.await_completed(hook_req)
assert(err == nil)
assert(hook_req ~= nil)

err, hook_req = sys.hook.create({ name = 'hook-name' })
assert(sys.await_failed(hook_req) == nil)
assert(err == nil)
assert(hook_req ~= nil)

_, hooks = sys.hook.list()
assert(#hooks == 1)

-- same name different namespace
err, hook_req = sys.hook.create({ name = 'hook-name', namespace_id = namespace_req.id })
assert(err == nil)
sys.await_completed(hook_req)

_, hooks = sys.hook.list()
assert(#hooks == 2)