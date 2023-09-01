sys = require('sys')

err, namespaces = sys.namespace.list()
assert(err == nil)

-- hamal as default namespace
assert(#namespaces == 1)

err, namespace_one_req = sys.namespace.create({
    name = 'namespace-1'
})

sys.await_completed(namespace_one_req)

assert(err == nil)
assert(namespace_one_req ~= nil)
--
_, namespaces = sys.namespace.list()
assert(#namespaces == 2)

assert(namespace_one_req.id == namespaces[1].id)
assert(namespaces[1].name == 'namespace-1')

_, namespace_two_req = sys.namespace.create({ name = 'another-namespace' })
sys.await_completed(namespace_two_req)

_, namespaces = sys.namespace.list()
assert(#namespaces == 3)
