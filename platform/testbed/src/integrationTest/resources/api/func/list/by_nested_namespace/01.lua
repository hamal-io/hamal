sys = require_plugin('std.sys')

namespace_one_req = fail_on_error(sys.namespaces.append({ name = 'namespace-one' }))
sys.await_completed(namespace_one_req)

namespace_two_req = fail_on_error(sys.namespaces.append({ name = 'namespace-two' }))
sys.await_completed(namespace_two_req)

sys.await_completed(fail_on_error(sys.funcs.create({ namespace_id = namespace_one_req.id, name = 'func-1' })))
sys.await_completed(fail_on_error(sys.funcs.create({ namespace_id = namespace_two_req.id, name = 'func-2' })))

assert(#fail_on_error(sys.funcs.list({ namespace_ids = { namespace_one_req.id } })) == 1)
assert(#fail_on_error(sys.funcs.list({ namespace_ids = { namespace_two_req.id } })) == 1)

-- root-namespace contains 2 namespace-one (func-1) and namespace_two(func-2)
root_namespace_hooks = fail_on_error(sys.funcs.list({ namespace_ids = { '539' } }))
assert(#root_namespace_hooks == 2)
assert(root_namespace_hooks[1].name == 'func-2')
assert(root_namespace_hooks[2].name == 'func-1')
