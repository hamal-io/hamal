sys = require_plugin('sys')

err, namespaces = sys.namespaces.list()
assert(err == nil)
-- hamal as default namespace
assert(#namespaces == 1)
assert(#namespaces[1].children == 0)

err, namespace = sys.namespaces.append({
    name = 'eth'
})
assert(err == nil)
sys.await_completed(namespace)

err, namespaces = sys.namespaces.list()
assert(err == nil)
assert(#namespaces == 1)
assert(#namespaces[1].children == 1)
assert(namespaces[1].children[1].name == 'eth')

---- tries to append namespace which is already there
err, namespace = sys.namespaces.append({
    name = 'eth'
})
assert(err == nil)
sys.await_failed(namespace)

-- nothing has changed
assert(err == nil)
assert(#namespaces == 1)
assert(#namespaces[1].children == 1)
assert(namespaces[1].children[1].name == 'eth')

err, namespace = sys.namespaces.append({
    name = 'btc'
})
assert(err == nil)
sys.await_completed(namespace)

err, namespaces = sys.namespaces.list()
assert(#namespaces == 1)
assert(#namespaces[1].children == 2)
assert(namespaces[1].children[1].name == 'eth')
assert(namespaces[1].children[2].name == 'btc')

