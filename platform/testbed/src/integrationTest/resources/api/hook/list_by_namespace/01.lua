sys = require_plugin('std.sys')

namespace_one_req = fail_on_error(sys.namespaces.append({ name = 'namespace-one' }))
sys.await_completed(namespace_one_req)

namespace_two_req = fail_on_error(sys.namespaces.append({ name = 'namespace-two' }))
sys.await_completed(namespace_two_req)

hook_one_req = fail_on_error(sys.hooks.create({ namespace_id = namespace_one_req.id, name = 'hook-1' }))
sys.await_completed(hook_one_req)

count = #fail_on_error(sys.hooks.list())
assert(count == 1)

count = #fail_on_error(sys.hooks.list({ }))
assert(count == 1)

count = #fail_on_error(sys.hooks.list({ namespace_ids = { namespace_one_req.id } }))
assert(count == 1)

count = #fail_on_error(sys.hooks.list({ namespace_ids = { namespace_two_req.id } }))
assert(count == 0)
