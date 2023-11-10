sys = require('sys')

err, flows = sys.flows.list()
assert(err == nil)

-- hamal as default flow
assert(#flows == 1)

err, flow_one_req = sys.flows.create({
    name = 'flow-1'
})

sys.await_completed(flow_one_req)

assert(err == nil)
assert(flow_one_req ~= nil)
--
_, flows = sys.flows.list()
assert(#flows == 2)

assert(flow_one_req.id == flows[1].id)
assert(flows[1].name == 'flow-1')

_, flow_two_req = sys.flows.create({ name = 'another-flow' })
sys.await_completed(flow_two_req)

_, flows = sys.flows.list()
assert(#flows == 3)