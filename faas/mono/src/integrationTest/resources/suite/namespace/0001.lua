local sys = require('sys')

local err, namespace_req = sys.namespace.create({
    name = 'empty-test-namespace',
    inputs = {},
    code = [[4 + 2]]
})

assert(err == nil)

assert(namespace_req.req_id ~= nil)
assert(namespace_req.status == 'Submitted')
assert(namespace_req.id ~= nil)

sys.await_completed(namespace_req)

local err, namespace = sys.namespace.get(namespace_req.id)
assert(err == nil)

assert(namespace.id == namespace_req.id)
assert(namespace.name == 'empty-test-namespace')
