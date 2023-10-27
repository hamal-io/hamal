sys = require('sys')

func_req = fail_on_error(sys.func.create({ namespace_id = '1'; name = 'test-func'; inputs = {}; code = [[4 + 2]] }))
sys.await_completed(func_req)

func_one_req = fail_on_error(sys.hook.create({ namespace_id = '1'; name = "some-amazing-hook" }))
sys.await(func_one_req)

trigger_req = fail_on_error(sys.trigger.create_hook({
    func_id = func_req.id,
    namespace_id = '1',
    name = 'trigger-to-create',
    inputs = { },
    hook_id = func_one_req.id
}))
sys.await_completed(trigger_req)

assert(trigger_req.req_id ~= nil)
assert(trigger_req.status == 'Submitted')
assert(trigger_req.id ~= nil)

trigger = fail_on_error(sys.trigger.get(trigger_req.id))

assert(trigger.type == 'Hook')
assert(trigger.name == 'trigger-to-create')
assert(trigger.func.name == "test-func")
assert(trigger.namespace.name == "hamal")
assert(trigger.hook.name == "some-amazing-hook")

triggers = fail_on_error(sys.trigger.list())
assert(#triggers == 1)

assert(triggers[1].type == 'Hook')
assert(triggers[1].name == 'trigger-to-create')
assert(triggers[1].func.name == 'test-func')
assert(triggers[1].namespace.name == "hamal")
assert(triggers[1].hook.name == "some-amazing-hook")
assert(#triggers[1].hook.methods == 1)
--assert(triggers[1].hook.methods[1] == 'Post')