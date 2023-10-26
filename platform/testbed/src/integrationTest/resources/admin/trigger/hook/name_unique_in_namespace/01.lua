sys = require('sys')

namespace_req = fail_on_error(sys.namespace.create({ name = 'namespace-1' }))
sys.await_completed(namespace_req)

create_func_req = fail_on_error(sys.func.create({ namespace_id = namespace_req.id; name = 'test-func'; inputs = {}; code = [[4 + 2]] }))
sys.await_completed(create_func_req)

func_one_req = fail_on_error(sys.hook.create({ namespace_id = '1'; name = "some-amazing-hook" }))
sys.await(func_one_req)

-- trigger name is unique
trigger_req = fail_on_error(sys.trigger.create_hook({
    func_id = create_func_req.id,
    namespace_id = '1',
    name = 'trigger-to-create',
    inputs = { },
    hook_id = func_one_req.id
}))
sys.await_completed(trigger_req)

trigger_req = fail_on_error(sys.trigger.create_hook({
    func_id = create_func_req.id,
    namespace_id = '1',
    name = 'trigger-to-create',
    inputs = { },
    hook_id = func_one_req.id
}))
assert(sys.await_failed(trigger_req) == nil)

_, triggers = sys.trigger.list()
assert(#triggers == 1)

-- same name different namespace
err, trigger_req = sys.trigger.create_hook({
    func_id = create_func_req.id,
    namespace_id = namespace_req.id,
    name = 'trigger-to-create',
    inputs = { },
    hook_id = func_one_req.id
})
assert(err == nil)
sys.await_completed(trigger_req)

_, triggers = sys.trigger.list()
assert(#triggers == 2)