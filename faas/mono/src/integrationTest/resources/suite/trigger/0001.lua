local sys = require('sys')

local err, create_func_req = sys.func.create({ name = 'empty-test-func'; inputs = {}; code = [[4 + 2]] })
assert(err == nil)
sys.await_completed(create_func_req)

local err, create_trigger_req = sys.trigger.create_fixed_rate({
    func_id = create_func_req.id,
    namespace_id = nil,
    name = 'trigger-to-create',
    inputs = { },
    duration = 'PT5S'
})
assert(err == nil)
sys.await_completed(create_trigger_req)

assert(create_trigger_req.req_id ~= nil)
assert(create_trigger_req.status == 'Submitted')
assert(create_trigger_req.id ~= nil)

local err, trigger = sys.trigger.get(create_trigger_req.id)
assert(err == nil)

assert(trigger.name == 'trigger-to-create')
assert(trigger.func.name == "empty-test-func")
assert(trigger.namespace.name == "hamal")
assert(trigger.duration == "PT5S")

local err, triggers = sys.trigger.list()
assert(err == nil)
assert(#triggers == 1)

assert(triggers[1].name == 'trigger-to-create')
assert(triggers[1].func.name == 'empty-test-func')
assert(triggers[1].namespace.name == "hamal")