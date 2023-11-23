sys = require('sys')

func_req = fail_on_error(sys.funcs.create({ name = 'test-func'; inputs = {}; code = [[4 + 2]] }))
sys.await_completed(func_req)

trigger_req = fail_on_error(sys.triggers.create_fixed_rate({
    func_id = func_req.func_id,
    name = 'trigger-to-create',
    inputs = { },
    duration = 'PT5S'
}))
sys.await_completed(trigger_req)

trigger = fail_on_error(sys.triggers.get(trigger_req.trigger_id))
assert(trigger.status == 'Active')

status_req = fail_on_error(sys.triggers.deactivate(trigger))
await_completed(status_req)
assert(trigger.status == 'Inactive')