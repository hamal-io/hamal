sys = require('sys')

err, namespace = sys.namespace.create({
    name = 'test-namespace',
    inputs = {},
})

assert(err == nil)

assert(namespace.req_id ~= nil)
assert(namespace.status == 'Submitted')
assert(namespace.id ~= nil)

sys.await_completed(namespace)

err, namespace = sys.namespace.get(namespace.id)
assert(err == nil)

assert(namespace.id == namespace.id)
assert(namespace.name == 'test-namespace')