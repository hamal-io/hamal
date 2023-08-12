local sys = require('sys')

local err, namespace_res = sys.namespace.create({
    name = 'empty-test-namespace',
    inputs = {},
    code = [[4 + 2]]
})

assert(err == nil)

assert(namespace_res.req_id ~= nil)
assert(namespace_res.status == 'Submitted')
assert(namespace_res.id ~= nil)

local err, namespace = sys.namespace.get(namespace_res.id)
assert(err == nil)

assert(namespace.id == namespace_res.id)
assert(namespace.name == 'empty-test-namespace')
