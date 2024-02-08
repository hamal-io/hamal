sys = require_plugin('sys')

create_req = fail_on_error(sys.namespaces.create({
    name = 'test-namespace',
    inputs = {}
}))

assert(create_req.id ~= nil)
assert(create_req.status == 'Submitted')
assert(create_req.namespace_id ~= nil)
sys.await_completed(create_req)

namespace = fail_on_error(sys.namespaces.get(create_req.namespace_id))

assert(namespace.id == create_req.namespace_id)
assert(namespace.name == 'test-namespace')
assert(namespace.type == '__default__')