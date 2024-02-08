sys = require_plugin('sys')

err, flows = sys.flows.list()
assert(err == nil)
-- hamal as default namespace
assert(#flows == 1)

err, flow = sys.flows.create({
    name = 'io::hamal::web3::eth'
})
assert(err == nil)
sys.await_completed(flow)

err, flows = sys.flows.list()
assert(err == nil)
assert(#flows == 2)

