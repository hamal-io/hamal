sys = require('sys')

one_req = fail_on_error(sys.flow.create({ name = 'flow-one' }))
sys.await_completed(one_req)

two_req = fail_on_error(sys.flow.create({ name = 'flow-two' }))
sys.await_completed(two_req)

func = fail_on_error(sys.func.create({ flow_id = one_req.id; name = 'test-func'; inputs = {}; code = [[4 + 2]] }))
sys.await_completed(func)

func = fail_on_error(sys.trigger.create_fixed_rate({
    func_id = func.id,
    flow_id = one_req.id,
    name = 'trigger-to-create',
    inputs = { },
    duration = 'PT5S'
}))
sys.await_completed(func)

count = #fail_on_error(sys.trigger.list())
assert(count == 1)

count = #fail_on_error(sys.trigger.list({ }))
assert(count == 1)

count = #fail_on_error(sys.trigger.list({ flow_ids = { one_req.id } }))
assert(count == 1)

count = #fail_on_error(sys.trigger.list({ flow_ids = { two_req.id } }))
assert(count == 0)
