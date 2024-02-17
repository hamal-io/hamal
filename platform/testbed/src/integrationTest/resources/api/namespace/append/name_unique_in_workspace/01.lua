sys = require_plugin('sys')

err, namespaces = sys.namespaces.list()
assert(err == nil)
-- hamal as default namespace
assert(#namespaces == 1)
assert(namespaces[1].name == 'root-namespace')

err, namespace = sys.namespaces.append({
    name = 'eth'
})
assert(err == nil)
sys.await_completed(namespace)

err, namespaces = sys.namespaces.list()
assert(err == nil)
assert(#namespaces == 2)
assert(namespaces[1].name == 'root-namespace')
assert(namespaces[2].name == 'root-namespace::eth')
--
------ tries to append namespace which is already there
err, namespace = sys.namespaces.append({
    name = 'eth'
})
assert(err == nil)
sys.await_failed(namespace)

-- nothing has changed
assert(err == nil)
assert(#namespaces == 2)
assert(namespaces[1].name == 'root-namespace')
assert(namespaces[2].name == 'root-namespace::eth')

err, namespace = sys.namespaces.append({
    name = 'btc'
})
assert(err == nil)
sys.await_completed(namespace)

err, namespaces = sys.namespaces.list()
assert(#namespaces == 3)
assert(namespaces[1].name == 'root-namespace')
assert(namespaces[2].name == 'root-namespace::eth')
assert(namespaces[3].name == 'root-namespace::btc')

