sys = require('sys')

_, create_func_req = sys.func.create({ name = 'test-func'; inputs = {}; code = [[4 + 2]] })
sys.await_completed(create_func_req)

_, hook_one_req = sys.hook.create({ name = "some-amazing-hook" })
sys.await(hook_one_req)

err, trigger_create_req = sys.trigger.create_hook({
    func_id = create_func_req.id,
    namespace_id = nil,
    name = 'trigger-to-create',
    inputs = { },
    hook_id = hook_one_req.id
})
assert(err == nil)
sys.await_completed(trigger_create_req)

assert(trigger_create_req.req_id ~= nil)
assert(trigger_create_req.status == 'Submitted')
assert(trigger_create_req.id ~= nil)

err, trigger = sys.trigger.get(trigger_create_req.id)
assert(err == nil)

assert(trigger.type == 'Hook')
assert(trigger.name == 'trigger-to-create')
assert(trigger.func.name == "test-func")
assert(trigger.namespace.name == "hamal")
assert(trigger.hook.name == "some-amazing-hook")

err, triggers = sys.trigger.list()
assert(err == nil)
assert(#triggers == 1)

assert(triggers[1].type == 'Hook')
assert(triggers[1].name == 'trigger-to-create')
assert(triggers[1].func.name == 'test-func')
assert(triggers[1].namespace.name == "hamal")
assert(triggers[1].hook.name == "some-amazing-hook")