sys = require_plugin('std.sys')

namespace_one_req = fail_on_error(sys.namespaces.append({ name = 'namespace-one' }))
sys.await_completed(namespace_one_req)

namespace_two_req = fail_on_error(sys.namespaces.append({ name = 'namespace-two' }))
sys.await_completed(namespace_two_req)

sys.await_completed(fail_on_error(sys.hooks.create({ namespace_id = namespace_one_req.id, name = 'hook-1' })))
sys.await_completed(fail_on_error(sys.hooks.create({ namespace_id = namespace_two_req.id, name = 'hook-2' })))

assert(#fail_on_error(sys.hooks.list({ namespace_ids = { namespace_one_req.id } })) == 1)
assert(#fail_on_error(sys.hooks.list({ namespace_ids = { namespace_two_req.id } })) == 1)

root_namespace_hooks = fail_on_error(sys.hooks.list({ namespace_ids = { '539' } }))
assert(#root_namespace_hooks == 2)
assert(root_namespace_hooks[1].name == 'hook-2')
assert(root_namespace_hooks[2].name == 'hook-1')
