local sys = require('sys')

local err, namespaces = sys.namespace.list()
assert(err == nil)
-- hamal as default namespace
assert(#namespaces == 1)

local err, namespace_req = sys.namespace.create({
    name = 'io::hamal::web3::eth'
})
assert(err == nil)
sys.await(namespace_req)

local err, namespaces = sys.namespace.list()
assert(err == nil)
assert(#namespaces == 5)


-- creates namespace were part of the namespace already exists
err, namespace_req = sys.namespace.create({
    name = 'io::hamal::web3::eth::user_1'
})
assert(err == nil)
sys.await(namespace_req)

-- nothing has changed
err, namespaces = sys.namespace.list()
assert(err == nil)
assert(#namespaces == 6)