sys = require('sys')

one_req = fail_on_error(sys.namespace.create({ name = 'namespace-one' }))
sys.await_completed(one_req)

two_req = fail_on_error(sys.namespace.create({ name = 'namespace-two' }))
sys.await_completed(two_req)

hook_one_req = fail_on_error(sys.hook.create({ namespace_id = one_req.id, name = 'hook-1' }))
sys.await_completed(hook_one_req)

count = #fail_on_error(sys.hook.list())
assert(count == 1)

count = #fail_on_error(sys.hook.list({ }))
assert(count == 1)

count = #fail_on_error(sys.hook.list({ namespace_ids = { one_req.id } }))
assert(count == 1)

count = #fail_on_error(sys.hook.list({ namespace_ids = { two_req.id } }))
assert(count == 0)
