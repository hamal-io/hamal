sys = require('sys')

namespace_req = fail_on_error(sys.namespace.create({ name = "hamal::name:space::rocks" }))
sys.await_completed(namespace_req)

func_req = fail_on_error(sys.func.create({ namespace_id = namespace_req.id, name = 'test-func'; inputs = {}; code = [[4 + 2]] }))
sys.await_completed(func_req)

trigger_req = fail_on_error(sys.trigger.create_fixed_rate({
    namespace_id = namespace_req.namespace_id,
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
assert(trigger_req.namespace_id == namespace_req.namespace_id)

trigger = fail_on_error(sys.trigger.get(trigger_req.trigger_id))

assert(trigger.type == 'FixedRate')
assert(trigger.name == 'trigger-to-create')
assert(trigger.func.name == 'test-func')
assert(trigger.namespace.name == "hamal::name:space::rocks")
assert(trigger.duration == "PT5S")

