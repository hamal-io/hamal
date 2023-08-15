local sys = require('sys')

local err, namespaces = sys.namespace.list()
assert(err == nil)
-- hamal as default namespace
assert(#namespaces == 1)

local err, namespace_req = sys.namespace.create({
    name = 'io::hamal::web3::eth'
})
assert(err == nil)
sys.await_completed(namespace_req)

err, namespace_req = sys.namespace.create({
    name = 'io::hamal::web3::eth'
})

assert(err == nil)
sys.await_completed(namespace_req)

local err, result = sys.req.get(namespace_req.req_id)
assert(err == nil)
assert(result.status == 'Completed')
