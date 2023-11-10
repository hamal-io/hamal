sys = require('sys')

one_req = fail_on_error(sys.flows.create({ name = 'flow-one' }))
sys.await_completed(one_req)

two_req = fail_on_error(sys.flows.create({ name = 'flow-two' }))
sys.await_completed(two_req)

hook_one_req = fail_on_error(sys.hooks.create({ flow_id = one_req.id, name = 'hook-1' }))
sys.await_completed(hook_one_req)

count = #fail_on_error(sys.hooks.list())
assert(count == 1)

count = #fail_on_error(sys.hooks.list({ }))
assert(count == 1)

count = #fail_on_error(sys.hooks.list({ flow_ids = { one_req.id } }))
assert(count == 1)

count = #fail_on_error(sys.hooks.list({ flow_ids = { two_req.id } }))
assert(count == 0)
