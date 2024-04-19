sys = require_plugin('std.sys')

err, namespaces = sys.namespaces.list()
assert(err == nil)
-- default as default namespace
assert(#namespaces == 1)

err, namespace = sys.namespaces.append({
    name = 'appended_namespace_name'
})
assert(err == nil)
sys.await_completed(namespace)

err, namespaces = sys.namespaces.list()
assert(err == nil)
assert(#namespaces == 2)
assert(namespaces[1].name == 'root-namespace::appended_namespace_name')
assert(namespaces[2].name == 'root-namespace')

--
