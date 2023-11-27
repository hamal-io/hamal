sys = require_plugin('sys')

func_req = fail_on_error(sys.funcs.create({ name = 'test-func'; inputs = {}; code = [[4 + 2]] }))
sys.await_completed(func_req)

trigger_req = fail_on_error(sys.triggers.create_fixed_rate({
    func_id = func_req.func_id,
    name = 'trigger-to-create',
    inputs = { },
    duration = 'PT5S'
}))
sys.await_completed(trigger_req)

assert(trigger_req.id ~= nil)
assert(trigger_req.status == 'Submitted')
assert(trigger_req.trigger_id ~= nil)
assert(trigger_req.group_id == '1')
assert(trigger_req.flow_id == '1')

trigger = fail_on_error(sys.triggers.get(trigger_req.trigger_id))
assert(trigger.type == 'FixedRate')
assert(trigger.name == 'trigger-to-create')
assert(trigger.func.name == "test-func")
assert(trigger.flow.id == '1')
assert(trigger.flow.name == "root-flow")
assert(trigger.duration == "PT5S")

