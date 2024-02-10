sys = require_plugin('sys')

namespace = fail_on_error(sys.namespaces.create({ name = 'test-namespace' }))
sys.await_completed(namespace)

res = fail_on_error(sys.adhoc({
    namespace_id = namespace.id,
    inputs = {},
    code = [[
        assert(0 ~= 1)
    ]]
}))
sys.await_completed(res)

assert(res.group_id == '1')
assert(res.namespace_id == namespace.id)