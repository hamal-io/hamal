sys = require_plugin('sys')

namespace_req = fail_on_error(sys.namespaces.create({ name = "hamal::namespace::rocks" }))
sys.await_completed(namespace_req)

func_req = fail_on_error(sys.funcs.create({ namespace_id = namespace_req.namespace_id, name = 'test-func'; inputs = {}; code = [[4 + 2]] }))
sys.await_completed(func_req)

req_one = fail_on_error(sys.triggers.create_cron({
    namespace_id = namespace_req.namespace_id,
    func_id = func_req.func_id,
    name = 'trigger-to-create',
    inputs = { },
    cron = '0 0 8-10 * * *'
}))
sys.await_completed(req_one)

assert(req_one.id ~= nil)
assert(req_one.status == 'Submitted')
assert(req_one.trigger_id ~= nil)
assert(req_one.group_id == '1')
assert(req_one.namespace_id == namespace_req.namespace_id)

req_two = fail_on_error(sys.triggers.get(req_one.trigger_id))
assert(req_two.type == 'Cron')
assert(req_two.name == 'trigger-to-create')
assert(req_two.func.name == "test-func")
assert(req_two.namespace.name == "hamal::namespace::rocks")
assert(req_two.cron == '0 0 8-10 * * *')

