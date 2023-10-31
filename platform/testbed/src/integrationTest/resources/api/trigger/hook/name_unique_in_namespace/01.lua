sys = require('sys')

namespace = fail_on_error(sys.namespace.create({ name = 'namespace-1' }))
sys.await_completed(namespace)

func = fail_on_error(sys.func.create({ namespace_id = namespace.id; name = 'test-func'; inputs = {}; code = [[4 + 2]] }))
sys.await_completed(func)

hook = fail_on_error(sys.hook.create({ namespace_id = '1'; name = "some-amazing-hook" }))
sys.await(hook)

-- trigger name is unique
trigger = fail_on_error(sys.trigger.create_hook({
    func_id = func.id,
    namespace_id = '1',
    name = 'trigger-to-create',
    inputs = { },
    hook_id = hook.id
}))
sys.await_completed(trigger)

trigger = fail_on_error(sys.trigger.create_hook({
    func_id = func.id,
    namespace_id = '1',
    name = 'trigger-to-create',
    inputs = { },
    hook_id = hook.id
}))
assert(sys.await_failed(trigger) == nil)

_, triggers = sys.trigger.list()
assert(#triggers == 1)

-- same name different namespace
err, trigger = sys.trigger.create_hook({
    func_id = func.id,
    namespace_id = namespace.id,
    name = 'trigger-to-create',
    inputs = { },
    hook_id = hook.id
})
assert(err == nil)
sys.await_completed(trigger)

_, triggers = sys.trigger.list()
assert(#triggers == 2)