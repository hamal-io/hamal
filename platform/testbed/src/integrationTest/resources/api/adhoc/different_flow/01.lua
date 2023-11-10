sys = require('sys')

flow = fail_on_error(sys.flow.create({ name = 'test-flow' }))
sys.await_completed(flow)

res = fail_on_error(sys.adhoc({
    flow_id = flow.id,
    inputs = {},
    code = [[
        assert(0 ~= 1)
    ]]
}))
sys.await_completed(res)

assert(res.group_id == '1')
assert(res.flow_id == flow.id)