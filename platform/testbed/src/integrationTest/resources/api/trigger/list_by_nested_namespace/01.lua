sys = require_plugin('sys')

namespace_one_req = fail_on_error(sys.namespaces.append({ name = 'namespace-one' }))
sys.await_completed(namespace_one_req)
func_one_req = fail_on_error(sys.funcs.create({ namespace_id = namespace_one_req.id, name = 'func-1' }))
sys.await_completed(func_one_req)
sys.await_completed(fail_on_error(sys.triggers.create_fixed_rate({
    namespace_id = namespace_one_req.id,
    name = 'trigger-1',
    func_id = func_one_req.func_id,
    inputs = { },
    duration = 'PT1S'
})))

namespace_two_req = fail_on_error(sys.namespaces.append({ name = 'namespace-two' }))
sys.await_completed(namespace_two_req)
func_two_req = fail_on_error(sys.funcs.create({ namespace_id = namespace_two_req.id, name = 'func-2' }))
sys.await_completed(func_two_req)
sys.await_completed(fail_on_error(sys.triggers.create_fixed_rate({
    namespace_id = namespace_two_req.id,
    name = 'trigger-2',
    func_id = func_two_req.func_id,
    inputs = { },
    duration = 'PT2S'
})))

assert(#fail_on_error(sys.triggers.list({ namespace_ids = { namespace_one_req.id } })) == 1)
assert(#fail_on_error(sys.triggers.list({ namespace_ids = { namespace_two_req.id } })) == 1)

root_namespace_triggers = fail_on_error(sys.triggers.list({ namespace_ids = { '1' } }))
assert(#root_namespace_triggers == 2)
assert(root_namespace_triggers[1].name == 'trigger-2')
assert(root_namespace_triggers[2].name == 'trigger-1')
