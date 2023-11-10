sys = require('sys')

err, req = sys.flow.create({
    name = 'test-flow',
    inputs = {},
})

assert(err == nil)

assert(req.id ~= nil)
assert(req.status == 'Submitted')
assert(req.flow_id ~= nil)

sys.await_completed(req)

flow = fail_on_error(sys.flow.get(req.flow_id))

assert(flow.id == req.flow_id)
assert(flow.name == 'test-flow')