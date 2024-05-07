sys = require_plugin('std.sys')

namespace_req = fail_on_error(sys.namespaces.append({
    name = 'test-namespace'
}))

assert(namespace_req.request_id ~= nil)
assert(namespace_req.request_status == 'Submitted')
assert(namespace_req.id ~= nil)
sys.await_completed(namespace_req)

namespace = fail_on_error(sys.namespaces.get(namespace_req.id))

assert(namespace.id == namespace_req.id)
assert(namespace.name == 'test-namespace')