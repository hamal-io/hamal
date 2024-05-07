sys = require_plugin('std.sys')

namespace_req = fail_on_error(sys.namespaces.append({ name = 'test-namespace' }))
sys.await_completed(namespace_req)

res = fail_on_error(sys.adhoc({
    namespace_id = namespace_req.id,
    inputs = {},
    code = [[
        assert(0 ~= 1)
    ]]
}))
sys.await_completed(res)

assert(res.workspace_id == '539')
assert(res.namespace_id == namespace_req.id)