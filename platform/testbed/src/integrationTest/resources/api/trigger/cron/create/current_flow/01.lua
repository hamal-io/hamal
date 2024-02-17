sys = require_plugin('sys')

func_req = fail_on_error(sys.funcs.create({ name = 'test-func'; inputs = {}; code = [[4 + 2]] }))
sys.await_completed(func_req)

req_one = fail_on_error(sys.triggers.create_cron({
    func_id = func_req.func_id,
    name = 'trigger-to-append',
    inputs = { },
    cron = '0 0 8-10 * * *'
}))
sys.await_completed(req_one)

assert(req_one.id ~= nil)
assert(req_one.status == 'Submitted')
assert(req_one.trigger_id ~= nil)
assert(req_one.workspace_id == '1')
assert(req_one.namespace_id == '1')

req_two = fail_on_error(sys.triggers.get(req_one.trigger_id))
assert(req_two.type == 'Cron')
assert(req_two.name == 'trigger-to-append')
assert(req_two.func.name == "test-func")
assert(req_two.namespace.id == '1')
assert(req_two.namespace.name == "root-namespace")
assert(req_two.cron == '0 0 8-10 * * *')