sys = require_plugin('sys')

flow_req = fail_on_error(sys.flows.create({ name = "hamal::flow::rocks" }))
sys.await_completed(flow_req)

func_req = fail_on_error(sys.funcs.create({ flow_id = flow_req.id, name = 'test-func'; inputs = {}; code = [[4 + 2]] }))
sys.await_completed(func_req)

hook = fail_on_error(sys.hooks.create({ flow_id = flow_req.id, name = "some-amazing-hook" }))
sys.await(hook)

req_one = fail_on_error(sys.triggers.create_hook({
    flow_id = flow_req.id,
    func_id = func_req.id,
    name = 'hook-trigger',
    inputs = { },
    hook_method = 'Get',
    hook_id = hook.id
}))
sys.await_completed(req_one)

assert(req_one.id ~= nil)
assert(req_one.status == 'Submitted')
assert(req_one.trigger_id ~= nil)
assert(req_one.group_id == '1')
assert(req_one.flow_id == flow_req.flow_id)

req_two = fail_on_error(sys.triggers.get(req_one.trigger_id))
assert(req_two.type == 'Hook')
assert(req_two.name == 'hook-trigger')
assert(req_two.func.name == "test-func")
assert(req_two.flow.id == flow_req.id)
assert(req_two.flow.name == "hamal::flow::rocks")
assert(req_two.hook.name == "some-amazing-hook")

