sys = require('sys')

err, req = sys.namespace.create({
    name = 'test-namespace',
    inputs = {},
})

assert(err == nil)

assert(req.id ~= nil)
assert(req.status == 'Submitted')
assert(req.namespace_id ~= nil)

sys.await_completed(req)

namespace = fail_on_error(sys.namespace.get(req.namespace_id))

assert(namespace.id == req.namespace_id)
assert(namespace.name == 'test-namespace')