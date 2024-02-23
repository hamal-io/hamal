sys = require_plugin('sys')

res = fail_on_error(sys.adhoc({
    inputs = {},
    code = [[
        assert(0 ~= 1)
    ]]
}))
sys.await_completed(res)

assert(res.workspace_id == '539')
assert(res.namespace_id == '539')

exec_id = res.id

