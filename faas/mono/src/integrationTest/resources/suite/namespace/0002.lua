local sys = require('sys')

local err, namespaces = sys.namespace.list()
assert(err == nil)
assert(#namespaces == 0)

local err, namespace_one = sys.namespace.create({
    name = 'namespace-1'
})
assert(err == nil)
assert(namespace_one ~= nil)
--
local _, namespaces = sys.namespace.list()
assert(#namespaces == 1)

assert(namespace_one.id == namespaces[1].id)
assert(namespaces[1].name == 'namespace-1')

sys.namespace.create({})

_, namespaces = sys.namespace.list()
assert(#namespaces == 2)
