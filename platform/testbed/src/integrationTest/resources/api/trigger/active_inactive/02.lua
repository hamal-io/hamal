sys = require('sys')

func_req = fail_on_error(sys.funcs.create({ name = 'test-func2'; inputs = {}; code = [[4 + 2]] }))
sys.await_completed(func_req)

trigger_req = fail_on_error(sys.triggers.create_fixed_rate({
    func_id = func_req.func_id,
    name = 'trigger-to-create2',
    inputs = { },
    duration = 'PT5S'
}))
sys.await_completed(trigger_req)

trigger = fail_on_error(sys.triggers.get(trigger_req.trigger_id))
assert(trigger.status == 'Active')

status_req = fail_on_error(sys.triggers.activate(trigger))
sys.await_completed(status_req)

trigger = fail_on_error(sys.triggers.get(trigger_req.trigger_id))
print(trigger.status)
assert(trigger.status == 'Active')
assert(trigger.type == 'FixedRate')
assert(trigger.name == 'trigger-to-create2')
assert(trigger.func.name == "test-func2")
assert(trigger.flow.id == '1')
assert(trigger.flow.name == "root-flow")
assert(trigger.duration == "PT5S")