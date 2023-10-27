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


-- tries to create namespace which is already there
err, namespace = sys.namespace.create({
    name = 'io::hamal::web3'
})
assert(err == nil)
sys.await_completed(namespace)

-- nothing has changed
err, namespaces = sys.namespace.list()
assert(err == nil)
assert(#namespaces == 5)