sys = require('sys')

err, flows = sys.flow.list()
assert(err == nil)
-- hamal as default flow
assert(#flows == 1)

err, flow = sys.flow.create({
    name = 'io::hamal::web3::eth'
})
assert(err == nil)
sys.await_completed(flow)

err, flows = sys.flow.list()
assert(err == nil)
assert(#flows == 2)

