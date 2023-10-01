admin = require('sys')

_, namespace_req = admin.namespace.create({ name = 'namespace-1' })

_, create_func_req = admin.func.create({ name = 'empty-test-func'; inputs = {}; code = [[4 + 2]] })
admin.await_completed(create_func_req)

-- trigger name is unique
err, trigger_req = admin.trigger.create_fixed_rate({
    func_id = create_func_req.id,
    namespace_id = nil,
    name = 'trigger-to-create',
    inputs = { },
    duration = 'PT5S'
})
admin.await_completed(trigger_req)
assert(err == nil)

err, trigger_req = admin.trigger.create_fixed_rate({
    func_id = create_func_req.id,
    namespace_id = nil,
    name = 'trigger-to-create',
    inputs = { },
    duration = 'PT5S'
})
assert(err == nil)
assert(admin.await_failed(trigger_req) == nil)

_, triggers = admin.trigger.list()
assert(#triggers == 1)
--
-- same name different namespace
err, trigger_req = admin.trigger.create_fixed_rate({
    func_id = create_func_req.id,
    namespace_id = namespace_req.id,
    name = 'trigger-to-create',
    inputs = { },
    duration = 'PT5S'
})
assert(err == nil)
admin.await_completed(trigger_req)
--
_, triggers = admin.trigger.list()
assert(#triggers == 2)
