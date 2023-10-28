sys = require('sys')

func_req = fail_on_error(sys.func.create({ name = 'test-func'; inputs = {}; code = [[4 + 2]] }))
sys.await_completed(func_req)

hook_req = fail_on_error(sys.hook.create({ name = "some-amazing-hook" }))
sys.await(hook_req)

trigger_req = fail_on_error(sys.trigger.create_hook({
    func_id = func_req.func_id,
    name = 'hook-trigger',
    inputs = { },
    hook_id = hook_req.hook_id
}))
sys.await_completed(trigger_req)

assert(trigger_req.id ~= nil)
assert(trigger_req.status == 'Submitted')
assert(trigger_req.trigger_id ~= nil)
assert(trigger_req.group_id == '1')
assert(trigger_req.namespace_id == '1')

trigger = fail_on_error(sys.trigger.get(trigger_req.trigger_id))
assert(trigger.type == 'Hook')
assert(trigger.name == 'hook-trigger')
assert(trigger.func.name == "test-func")
assert(trigger.namespace.id == '1')
assert(trigger.namespace.name == "root-namespace")
assert(trigger.hook.name == "some-amazing-hook")

