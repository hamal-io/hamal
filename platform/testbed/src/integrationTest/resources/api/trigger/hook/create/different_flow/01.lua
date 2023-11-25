sys = require('sys')

flow_req = fail_on_error(sys.flows.create({ name = "hamal::flow::rocks" }))
sys.await_completed(flow_req)

func_req = fail_on_error(sys.funcs.create({ flow_id = flow_req.id, name = 'test-func'; inputs = {}; code = [[4 + 2]] }))
sys.await_completed(func_req)

hook_req = fail_on_error(sys.hooks.create({ flow_id = flow_req.id, name = "some-amazing-hook" }))
sys.await(hook_req)

trigger_req = fail_on_error(sys.triggers.create_hook({
    flow_id = flow_req.id,
    func_id = func_req.id,
    name = 'hook-trigger',
    inputs = { },
    hook_id = hook_req.id,
    hook_method = 'GET'
}))
sys.await_completed(trigger_req)

assert(trigger_req.id ~= nil)
assert(trigger_req.status == 'Submitted')
assert(trigger_req.trigger_id ~= nil)
assert(trigger_req.group_id == '1')
assert(trigger_req.flow_id == flow_req.flow_id)

trigger = fail_on_error(sys.triggers.get(trigger_req.trigger_id))
assert(trigger.type == 'Hook')
assert(trigger.name == 'hook-trigger')
assert(trigger.func.name == "test-func")
assert(trigger.flow.id == flow_req.id)
assert(trigger.flow.name == "hamal::flow::rocks")
assert(trigger.hook.name == "some-amazing-hook")

