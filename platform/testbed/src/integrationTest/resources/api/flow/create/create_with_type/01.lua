sys = require_plugin('sys')

create_req = fail_on_error(sys.flows.create({
    name = 'test-flow',
    inputs = {},
    type = '__special__'
}))

assert(create_req.id ~= nil)
assert(create_req.status == 'Submitted')
assert(create_req.flow_id ~= nil)
sys.await_completed(create_req)

flow = fail_on_error(sys.flows.get(create_req.flow_id))

assert(flow.id == create_req.flow_id)
assert(flow.name == 'test-flow')
assert(flow.type == '__special__')