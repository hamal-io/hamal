sys = require_plugin('sys')

err, namespaces = sys.namespaces.list()
assert(err == nil)
-- hamal as default namespace
assert(#namespaces == 1)

err, namespace = sys.namespaces.create({
    name = 'io::hamal::web3::eth'
})
assert(err == nil)
sys.await_completed(namespace)

err, namespaces = sys.namespaces.list()
assert(err == nil)
assert(#namespaces == 2)

-- tries to create namespace which is already there
err, namespace = sys.namespaces.create({
    name = 'io::hamal::web3::eth'
})
assert(err == nil)
sys.await_failed(namespace)

-- nothing has changed
err, namespaces = sys.namespaces.list()
assert(err == nil)
assert(#namespaces == 2)

-- tries to create namespace which is already there
err, namespace = sys.namespaces.create({
    name = 'io::hamal::web3::eth::sub'
})
assert(err == nil)
sys.await_completed(namespace)


-- nothing has changed
err, namespaces = sys.namespaces.list()
assert(err == nil)
assert(#namespaces == 3)
