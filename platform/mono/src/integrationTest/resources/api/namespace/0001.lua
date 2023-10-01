admin = require('sys')

err, namespace_req = admin.namespace.create({
    name = 'empty-test-namespace',
    inputs = {},
    code = [[4 + 2]]
})

assert(err == nil)

assert(namespace_req.req_id ~= nil)
assert(namespace_req.status == 'Submitted')
assert(namespace_req.id ~= nil)

admin.await_completed(namespace_req)

err, namespace = admin.namespace.get(namespace_req.id)
assert(err == nil)

assert(namespace.id == namespace_req.id)
assert(namespace.name == 'empty-test-namespace')
