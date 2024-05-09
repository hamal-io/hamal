sys = require_plugin('std.sys')

func_req = fail_on_error(sys.funcs.create({ name = 'test-func'; inputs = {}; code = [[4 + 2]] }))
sys.await_completed(func_req)

trigger_req = fail_on_error(sys.triggers.create_fixed_rate({
    func_id = func_req.id,
    name = 'trigger-one',
    inputs = { },
    duration = 'PT5S'
}))
sys.await_completed(trigger_req)

trigger = fail_on_error(sys.triggers.get(trigger_req.id))

trigger_req = fail_on_error(sys.triggers.delete(trigger))
sys.await_completed(trigger_req)
assert(trigger_req.id == trigger.id)
assert(trigger_req.request_status == 'Submitted')

err, trigger = sys.triggers.get(trigger.id)
assert(err.message == 'Trigger not found')
assert(trigger == nil)

count = #fail_on_error(sys.triggers.list())
assert(count == 0)
