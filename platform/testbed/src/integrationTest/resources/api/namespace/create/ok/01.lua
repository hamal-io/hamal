sys = require_plugin('sys')

err, namespaces = sys.namespaces.list()
assert(err == nil)
-- default as default namespace
assert(#namespaces == 1)

err, namespace = sys.namespaces.create({
    name = 'io::hamal::web3::eth'
})
assert(err == nil)
sys.await_completed(namespace)

err, namespaces = sys.namespaces.list()
assert(err == nil)
assert(#namespaces == 2)

