sys = require('sys')

func = fail_on_error(sys.func.create({ name = 'test-func'; inputs = {}; code = [[4 + 2]] }))
sys.await_completed(func)

hook = fail_on_error(sys.hook.create({ name = "some-amazing-hook" }))
sys.await(hook)

trigger = fail_on_error(sys.trigger.create_hook({
    func_id = func.id,
    name = 'hook-trigger',
    inputs = { },
    hook_id = hook.id
}))
sys.await_completed(trigger)

assert(trigger.req_id ~= nil)
assert(trigger.status == 'Submitted')
assert(trigger.id ~= nil)
assert(trigger.group_id == '1')
assert(trigger.namespace_id == '1')

trigger = fail_on_error(sys.trigger.get(trigger.id))
assert(trigger.type == 'Hook')
assert(trigger.name == 'hook-trigger')
assert(trigger.func.name == "test-func")
assert(trigger.namespace.id == '1')
assert(trigger.namespace.name == "root-namespace")
assert(trigger.hook.name == "some-amazing-hook")

