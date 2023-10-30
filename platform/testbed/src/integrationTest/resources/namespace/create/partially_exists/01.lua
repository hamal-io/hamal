sys = require('sys')

err, namespaces = sys.namespace.list()
assert(err == nil)
-- hamal as default namespace
assert(#namespaces == 1)

err, namespace = sys.namespace.create({
    name = 'io::hamal::web3::eth'
})
assert(err == nil)
sys.await_completed(namespace)

err, namespaces = sys.namespace.list()
assert(err == nil)
assert(#namespaces == 5)


-- creates namespace were part of the namespace already exists
err, namespace = sys.namespace.create({
    name = 'io::hamal::web3::eth::user_1'
})
assert(err == nil)
sys.await_completed(namespace)

-- nothing has changed
err, namespaces = sys.namespace.list()
assert(err == nil)
assert(#namespaces == 6)