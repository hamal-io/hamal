sys = require_plugin('sys')

namespace_one_req = fail_on_error(sys.namespaces.append({ name = 'namespace-one' }))
sys.await_completed(namespace_one_req)

namespace_two_req = fail_on_error(sys.namespaces.append({ name = 'namespace-two' }))
sys.await_completed(namespace_two_req)

sys.await_completed(
    fail_on_error(sys.funcs.create({ namespace_id = namespace_one_req.id, name = 'func-1' }))
)

count = #fail_on_error(sys.funcs.list())
assert(count == 1)

count = #fail_on_error(sys.funcs.list({}))
assert(count == 1)

count = #fail_on_error(sys.funcs.list({ namespace_ids = { namespace_one_req.id } }))
assert(count == 1)

count = #fail_on_error(sys.funcs.list({ namespace_ids = { namespace_two_req.id } }))
assert(count == 0)
