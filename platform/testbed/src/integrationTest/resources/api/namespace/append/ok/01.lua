sys = require_plugin('sys')

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
assert(#namespaces == 1)
assert(#namespaces[1].children == 1)
assert(namespaces[1].children[1].name == 'appended_namespace_name')

