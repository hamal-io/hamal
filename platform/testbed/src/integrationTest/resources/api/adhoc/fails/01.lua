sys = require_plugin('std.sys')

res = fail_on_error(sys.adhoc({
    inputs = {},
    code = [[
        assert( 0 ~= 0 )
    ]]
}))
sys.await_completed(res)

assert(res.workspace_id == '539')
assert(res.namespace_id == '539')

exec_id = res.id

