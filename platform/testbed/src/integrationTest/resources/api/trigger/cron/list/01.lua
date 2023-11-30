sys = require_plugin('sys')

func_req = fail_on_error(sys.funcs.create({ name = 'test-func'; inputs = {}; code = [[4 + 2]] }))
sys.await_completed(func_req)

req_one = fail_on_error(sys.triggers.create_cron({
    func_id = func_req.func_id,
    name = 'trigger-to-create',
    inputs = { },
    cron = '0 0 8-10 * * *'
}))
sys.await_completed(req_one)

triggers = fail_on_error(sys.triggers.list())
assert(#triggers == 1)

req_two = triggers[1]
assert(req_two.type == 'Cron')
assert(req_two.name == 'trigger-to-create')
assert(req_two.func.name == "test-func")
assert(req_two.flow.id == '1')
assert(req_two.flow.name == "root-flow")
assert(req_two.cron == '0 0 8-10 * * *')