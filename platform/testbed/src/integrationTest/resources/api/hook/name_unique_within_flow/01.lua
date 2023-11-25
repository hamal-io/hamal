sys = require('sys')

--hook name is unique
hook = fail_on_error(sys.hooks.create({ flow_id = '1'; name = 'hook-name' }))
sys.await_completed(hook)
assert(hook ~= nil)

_, hook = sys.hooks.create({ flow_id = '1'; name = 'hook-name' })
assert(sys.await_failed(hook) == nil)
assert(hook ~= nil)

_, hooks = sys.hooks.list()
assert(#hooks == 1)

-- same name different flow
flow = fail_on_error(sys.flows.create({ name = 'flow-1' }))
sys.await_completed(flow)

hook = fail_on_error(sys.hooks.create({ name = 'hook-name', flow_id = flow.id }))
sys.await_completed(hook)

_, hooks = sys.hooks.list()
assert(#hooks == 2)