sys = require_plugin('sys')
--
flow = fail_on_error(sys.flows.create({ name = "hamal::namespace::rocks" }))
sys.await_completed(flow)

func_one = fail_on_error(sys.funcs.create({
    flow_id = flow.id,
    name = 'func-1'
}))
sys.await_completed(func_one)

assert(func_one ~= nil)
assert(func_one.group_id == '1')
assert(func_one.flow_id == flow.id)

func_one = fail_on_error(sys.funcs.get(func_one.id))
assert(func_one.flow.id == flow.id)
assert(func_one.flow.name == "hamal::namespace::rocks")

err, hooks = sys.funcs.list()
assert(#hooks == 1)