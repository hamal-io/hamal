sys = require_plugin('std.sys')

namespace = fail_on_error(sys.namespaces.append({ name = 'namespace-1' }))
sys.await_completed(namespace)

func_one = fail_on_error(sys.funcs.create({ namespace_id = namespace.id; name = 'test-func'; inputs = {}; code = [[ x = 4 + 2]] }))
sys.await_completed(func_one)

-- trigger name is unique
req_two = fail_on_error(sys.triggers.create_fixed_rate({
    func_id = func_one.id,
    namespace_id = '539',
    name = 'trigger-to-append',
    inputs = { },
    duration = 'PT5S'
}))
sys.await_completed(req_two)

req_two = fail_on_error(sys.triggers.create_fixed_rate({
    func_id = func_one.id,
    namespace_id = '539',
    name = 'trigger-to-append',
    inputs = { },
    duration = 'PT5S'
}))
assert(sys.await_failed(req_two) == nil)

_, triggers = sys.triggers.list()
assert(#triggers == 1)
--
-- same name different namespace
req_two = fail_on_error(sys.triggers.create_fixed_rate({
    func_id = func_one.id,
    namespace_id = namespace.id,
    name = 'trigger-to-append',
    inputs = { },
    duration = 'PT5S'
}))
sys.await_completed(req_two)
--
_, triggers = sys.triggers.list()
assert(#triggers == 2)
