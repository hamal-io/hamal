sys = require('sys')

one_req = fail_on_error(sys.namespace.create({ name = 'namespace-one' }))
sys.await_completed(one_req)

two_req = fail_on_error(sys.namespace.create({ name = 'namespace-two' }))
sys.await_completed(two_req)

func = fail_on_error(sys.func.create({ namespace_id = one_req.id; name = 'test-func'; inputs = {}; code = [[4 + 2]] }))
sys.await_completed(func)

func = fail_on_error(sys.trigger.create_fixed_rate({
    func_id = func.id,
    namespace_id = one_req.id,
    name = 'trigger-to-create',
    inputs = { },
    duration = 'PT5S'
}))
sys.await_completed(func)

count = #fail_on_error(sys.trigger.list())
assert(count == 1)

count = #fail_on_error(sys.trigger.list({ }))
assert(count == 1)

count = #fail_on_error(sys.trigger.list({ namespace_ids = { one_req.id } }))
assert(count == 1)

count = #fail_on_error(sys.trigger.list({ namespace_ids = { two_req.id } }))
assert(count == 0)
