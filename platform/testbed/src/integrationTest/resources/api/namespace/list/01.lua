sys = require_plugin('sys')

err, namespaces = sys.namespaces.list()
assert(err == nil)
-- hamal as default namespace
assert(#namespaces == 1)

err, namespace_one_req = sys.namespaces.append({
    name = 'namespace-1'
})

sys.await_completed(namespace_one_req)
assert(err == nil)
assert(namespace_one_req ~= nil)
--
_, namespaces = sys.namespaces.list()
assert(#namespaces == 2)
assert(namespaces[1].name == 'root-namespace')

assert(namespaces[2].parent_id == namespaces[1].id)
assert(namespaces[2].name == 'root-namespace::namespace-1')

_, namespace_two_req = sys.namespaces.append({ name = 'another-namespace' })
sys.await_completed(namespace_two_req)

_, namespaces = sys.namespaces.list()
assert(#namespaces == 3)