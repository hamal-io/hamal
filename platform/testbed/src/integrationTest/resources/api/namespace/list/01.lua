sys = require_plugin('sys')

err, flows = sys.flows.list()
assert(err == nil)

-- hamal as default namespace
assert(#flows == 1)

err, flow_one_req = sys.flows.create({
    name = 'namespace-1'
})

sys.await_completed(flow_one_req)

assert(err == nil)
assert(flow_one_req ~= nil)
--
_, flows = sys.flows.list()
assert(#flows == 2)

assert(flow_one_req.id == flows[1].id)
assert(flows[1].name == 'namespace-1')

_, flow_two_req = sys.flows.create({ name = 'another-namespace' })
sys.await_completed(flow_two_req)

_, flows = sys.flows.list()
assert(#flows == 3)