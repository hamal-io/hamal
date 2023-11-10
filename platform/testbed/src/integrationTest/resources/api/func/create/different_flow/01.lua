sys = require('sys')
--
flow = fail_on_error(sys.flow.create({ name = "hamal::name:space::rocks" }))
sys.await_completed(flow)

func = fail_on_error(sys.func.create({
    flow_id = flow.id,
    name = 'func-1'
}))
sys.await_completed(func)

assert(func ~= nil)
assert(func.group_id == '1')
assert(func.flow_id == flow.id)

func = fail_on_error(sys.func.get(func.id))
assert(func.flow.id == flow.id)
assert(func.flow.name == "hamal::name:space::rocks")

err, hooks = sys.func.list()
assert(#hooks == 1)