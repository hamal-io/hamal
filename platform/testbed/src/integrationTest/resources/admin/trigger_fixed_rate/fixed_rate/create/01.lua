sys = require('sys')

err, create_func_req = sys.func.create({ name = 'empty-test-func'; inputs = {}; code = [[4 + 2]] })
assert(err == nil)
sys.await_completed(create_func_req)

err, trigger_create_req = sys.trigger.create_fixed_rate({
    func_id = create_func_req.id,
    namespace_id = nil,
    name = 'trigger-to-create',
    inputs = { },
    duration = 'PT5S'
})
assert(err == nil)
sys.await_completed(trigger_create_req)

assert(trigger_create_req.req_id ~= nil)
assert(trigger_create_req.status == 'Submitted')
assert(trigger_create_req.id ~= nil)

err, trigger = sys.trigger.get(trigger_create_req.id)
assert(err == nil)

assert(trigger.type == 'FixedRate')
assert(trigger.name == 'trigger-to-create')
assert(trigger.func.name == "empty-test-func")
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