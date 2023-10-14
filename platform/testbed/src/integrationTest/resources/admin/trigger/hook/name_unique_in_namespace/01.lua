sys = require('sys')

_, namespace_req = sys.namespace.create({ name = 'namespace-1' })

_, create_func_req = sys.func.create({ name = 'test-func'; inputs = {}; code = [[4 + 2]] })
sys.await_completed(create_func_req)

_, hook_one_req = sys.hook.create({ name = "some-amazing-hook" })
sys.await(hook_one_req)

-- trigger name is unique
err, trigger_req = sys.trigger.create_hook({
    func_id = create_func_req.id,
    namespace_id = nil,
    name = 'trigger-to-create',
    inputs = { },
    hook_id = hook_one_req.id
})
sys.await_completed(trigger_req)
assert(err == nil)

err, trigger_req = sys.trigger.create_hook({
    func_id = create_func_req.id,
    namespace_id = nil,
    name = 'trigger-to-create',
    inputs = { },
    hook_id = hook_one_req.id
})
assert(err == nil)
assert(sys.await_failed(trigger_req) == nil)

_, triggers = sys.trigger.list()
assert(#triggers == 1)

-- same name different namespace
err, trigger_req = sys.trigger.create_hook({
    func_id = create_func_req.id,
    namespace_id = namespace_req.id,
    name = 'trigger-to-create',
    inputs = { },
    hook_id = hook_one_req.id
})
assert(err == nil)
sys.await_completed(trigger_req)

_, triggers = sys.trigger.list()
assert(#triggers == 2)