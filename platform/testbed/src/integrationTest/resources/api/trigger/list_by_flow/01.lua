sys = require_plugin('sys')

one_req = fail_on_error(sys.namespaces.create({ name = 'namespace-one' }))
sys.await_completed(one_req)

two_req = fail_on_error(sys.namespaces.create({ name = 'namespace-two' }))
sys.await_completed(two_req)

func_one = fail_on_error(sys.funcs.create({ namespace_id = one_req.id; name = 'test-func'; inputs = {}; code = [[4 + 2]] }))
sys.await_completed(func_one)

func_one = fail_on_error(sys.triggers.create_fixed_rate({
    func_id = func_one.id,
    namespace_id = one_req.id,
    name = 'trigger-to-create',
    inputs = { },
    duration = 'PT5S'
}))
sys.await_completed(func_one)

count = #fail_on_error(sys.triggers.list())
assert(count == 1)

count = #fail_on_error(sys.triggers.list({ }))
assert(count == 1)

count = #fail_on_error(sys.triggers.list({ namespace_ids = { one_req.id } }))
assert(count == 1)

count = #fail_on_error(sys.triggers.list({ namespace_ids = { two_req.id } }))
assert(count == 0)
