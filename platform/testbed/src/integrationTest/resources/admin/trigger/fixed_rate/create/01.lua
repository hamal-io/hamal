sys = require('sys')

func_req = fail_on_error(sys.func.create({ namespace_id = '1'; name = 'empty-test-func'; inputs = {}; code = [[4 + 2]] }))
sys.await_completed(func_req)

trigger_req = fail_on_error(sys.trigger.create_fixed_rate({
    func_id = func_req.id,
    namespace_id = '1',
    name = 'trigger-to-create',
    inputs = { },
    duration = 'PT5S'
}))
sys.await_completed(trigger_req)

assert(trigger_req.req_id ~= nil)
assert(trigger_req.status == 'Submitted')
assert(trigger_req.id ~= nil)

trigger = fail_on_error(sys.trigger.get(trigger_req.id))

assert(trigger.type == 'FixedRate')
assert(trigger.name == 'trigger-to-create')
assert(trigger.func.name == "empty-test-func")
assert(trigger.namespace.id == '1')
assert(trigger.namespace.name == "hamal")
assert(trigger.duration == "PT5S")

err, triggers = sys.trigger.list()
assert(err == nil)
assert(#triggers == 1)

assert(triggers[1].type == 'FixedRate')
assert(triggers[1].name == 'trigger-to-create')
assert(triggers[1].func.name == 'empty-test-func')
assert(triggers[1].namespace.name == "hamal")
assert(triggers[1].duration == "PT5S")