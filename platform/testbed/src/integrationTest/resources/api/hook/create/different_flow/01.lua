sys = require('sys')
--
flow = fail_on_error(sys.flows.create({ name = "hamal::flow::rocks" }))
sys.await_completed(flow)

hook_req = fail_on_error(sys.hooks.create({
    flow_id = flow.id,
    name = 'hook-1'
}))
sys.await_completed(hook_req)

assert(hook_req ~= nil)
assert(hook_req.group_id == '1')
assert(hook_req.flow_id == flow.id)

_, hook = sys.hooks.get(hook_req.id)
assert(hook.flow.id == flow.id)
assert(hook.flow.name == "hamal::flow::rocks")

err, hooks = sys.hooks.list()
assert(#hooks == 1)