sys = require('sys')

namespace = fail_on_error(sys.namespace.create({ name = "hamal::name:space::rocks" }))
sys.await_completed(namespace)

func = fail_on_error(sys.func.create({ namespace_id = namespace.id, name = 'test-func'; inputs = {}; code = [[4 + 2]] }))
sys.await_completed(func)

trigger = fail_on_error(sys.trigger.create_fixed_rate({
    namespace_id = namespace.id,
    func_id = func.id,
    name = 'trigger-to-create',
    inputs = { },
    duration = 'PT5S'
}))
sys.await_completed(trigger)

assert(trigger.req_id ~= nil)
assert(trigger.status == 'Submitted')
assert(trigger.id ~= nil)
assert(trigger.group_id == '1')
assert(trigger.namespace_id == namespace.id)

err, trigger = sys.trigger.get(trigger.id)
assert(err == nil)

assert(trigger.type == 'FixedRate')
assert(trigger.name == 'trigger-to-create')
assert(trigger.func.name == 'test-func')
assert(trigger.namespace.name == "hamal::name:space::rocks")
assert(trigger.duration == "PT5S")

