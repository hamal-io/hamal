sys = require('sys')

namespace_req = fail_on_error(sys.namespace.create({ name = 'namespace-1' }))
sys.await_completed(namespace_req)

create_func_req = fail_on_error(sys.func.create({ namespace_id = namespace_req.id; name = 'empty-test-func'; inputs = {}; code = [[ x = 4 + 2]] }))
sys.await_completed(create_func_req)

-- trigger name is unique
trigger_req = fail_on_error(sys.trigger.create_fixed_rate({
    func_id = create_func_req.id,
    namespace_id = '1',
    name = 'trigger-to-create',
    inputs = { },
    duration = 'PT5S'
}))
sys.await_completed(trigger_req)

trigger_req = fail_on_error(sys.trigger.create_fixed_rate({
    func_id = create_func_req.id,
    namespace_id = '1',
    name = 'trigger-to-create',
    inputs = { },
    duration = 'PT5S'
}))
assert(sys.await_failed(trigger_req) == nil)

_, triggers = sys.trigger.list()
assert(#triggers == 1)
--
-- same name different namespace
trigger_req = fail_on_error(sys.trigger.create_fixed_rate({
    func_id = create_func_req.id,
    namespace_id = namespace_req.id,
    name = 'trigger-to-create',
    inputs = { },
    duration = 'PT5S'
}))
sys.await_completed(trigger_req)
--
_, triggers = sys.trigger.list()
assert(#triggers == 2)
