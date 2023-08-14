local sys = require('sys')

local err, namespaces = sys.namespace.list()
assert(err == nil)
assert(#namespaces == 0)

local err, namespace_req = sys.namespace.create({
    name = 'io::hamal::web3::eth'
})
assert(err == nil)
sys.await(namespace_req)

local err, namespaces = sys.namespace.list()
assert(err == nil)
assert(#namespaces == 4)


-- creates namespace were part of the namespace already exists
err, namespace_req = sys.namespace.create({
    name = 'io::hamal::web3::eth::user_1'
})
assert(err == nil)
sys.await(namespace_req)

-- nothing has changed
err, namespaces = sys.namespace.list()
assert(err == nil)
assert(#namespaces == 5)