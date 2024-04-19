sys = require_plugin('std.sys')

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
assert(namespaces[1].name == 'root-namespace::eth')
assert(namespaces[2].name == 'root-namespace')
--
------ tries to append namespace which is already there
err, namespace = sys.namespaces.append({
    name = 'eth'
})
assert(err == nil)
sys.await_completed(namespace)

err, namespaces = sys.namespaces.list()
assert(err == nil)
assert(#namespaces == 3)
assert(namespaces[1].name == 'root-namespace::eth')
assert(namespaces[2].name == 'root-namespace::eth')
assert(namespaces[3].name == 'root-namespace')

err, namespace = sys.namespaces.append({
    name = 'btc'
})
assert(err == nil)
sys.await_completed(namespace)

err, namespaces = sys.namespaces.list()
assert(#namespaces == 4)
assert(namespaces[1].name == 'root-namespace::btc')
assert(namespaces[2].name == 'root-namespace::eth')
assert(namespaces[3].name == 'root-namespace::eth')
assert(namespaces[4].name == 'root-namespace')

