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

-- tries to create namespace which is already there
err, flow = sys.flows.create({
    name = 'io::hamal::web3::eth'
})
assert(err == nil)
sys.await_failed(flow)

-- nothing has changed
err, flows = sys.flows.list()
assert(err == nil)
assert(#flows == 2)

-- tries to create namespace which is already there
err, flow = sys.flows.create({
    name = 'io::hamal::web3::eth::sub'
})
assert(err == nil)
sys.await_completed(flow)


-- nothing has changed
err, flows = sys.flows.list()
assert(err == nil)
assert(#flows == 3)
