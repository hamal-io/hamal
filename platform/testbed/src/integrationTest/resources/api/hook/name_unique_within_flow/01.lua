sys = require('sys')

--hook name is unique
hook_req = fail_on_error(sys.hook.create({ flow_id = '1'; name = 'hook-name' }))
sys.await_completed(hook_req)
assert(hook_req ~= nil)

_, hook_req = sys.hook.create({ flow_id = '1'; name = 'hook-name' })
assert(sys.await_failed(hook_req) == nil)
assert(hook_req ~= nil)

_, hooks = sys.hook.list()
assert(#hooks == 1)

-- same name different flow
flow = fail_on_error(sys.flow.create({ name = 'flow-1' }))
sys.await_completed(flow)

hook_req = fail_on_error(sys.hook.create({ name = 'hook-name', flow_id = flow.id }))
sys.await_completed(hook_req)

_, hooks = sys.hook.list()
assert(#hooks == 2)