sys = require('sys')

namespace_req = fail_on_error(sys.namespace.create({ name = 'namespace-1' }))
sys.await_completed(namespace_req)

create_func_req = fail_on_error(sys.func.create({ namespace_id = namespace_req.id; name = 'empty-test-func'; inputs = {}; code = [[ x = 4 + 2]] }))
sys.await_completed(create_func_req)

-- trigger name is unique
err, trigger_req = sys.trigger.create_fixed_rate({
    func_id = create_func_req.id,
    namespace_id = nil,
    name = 'trigger-to-create',
    inputs = { },
    duration = 'PT5S'
})

assert(err == nil)
sys.await_completed(trigger_req)

err, trigger_req = sys.trigger.create_fixed_rate({
    func_id = create_func_req.id,
    namespace_id = nil,
    name = 'trigger-to-create',
    inputs = { },
    duration = 'PT5S'
})
assert(err == nil)
assert(sys.await_failed(trigger_req) == nil)

_, triggers = sys.trigger.list()
assert(#triggers == 1)
--
-- same name different namespace
err, trigger_req = sys.trigger.create_fixed_rate({
    func_id = create_func_req.id,
    namespace_id = namespace_req.id,
    name = 'trigger-to-create',
    inputs = { },
    duration = 'PT5S'
})
assert(err == nil)
sys.await_completed(trigger_req)
--
_, triggers = sys.trigger.list()
assert(#triggers == 2)
