sys = require_plugin('sys')

res = fail_on_error(sys.adhoc({
    inputs = {},
    code = [[
        assert(0 ~= 1)
    ]]
}))
sys.await_completed(res)

assert(res.group_id == '1')
assert(res.flow_id == '1')

exec_id = res.id

