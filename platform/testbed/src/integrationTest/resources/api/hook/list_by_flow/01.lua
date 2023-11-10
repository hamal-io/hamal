sys = require('sys')

one_req = fail_on_error(sys.flow.create({ name = 'flow-one' }))
sys.await_completed(one_req)

two_req = fail_on_error(sys.flow.create({ name = 'flow-two' }))
sys.await_completed(two_req)

hook_one_req = fail_on_error(sys.hook.create({ flow_id = one_req.id, name = 'hook-1' }))
sys.await_completed(hook_one_req)

count = #fail_on_error(sys.hook.list())
assert(count == 1)

count = #fail_on_error(sys.hook.list({ }))
assert(count == 1)

count = #fail_on_error(sys.hook.list({ flow_ids = { one_req.id } }))
assert(count == 1)

count = #fail_on_error(sys.hook.list({ flow_ids = { two_req.id } }))
assert(count == 0)
