sys = require_plugin('std.sys')

func_req = fail_on_error(sys.funcs.create({ name = 'test-func2'; inputs = {}; code = [[4 + 2]] }))
sys.await_completed(func_req)

req_one = fail_on_error(sys.triggers.create_fixed_rate({
    func_id = func_req.id,
    name = 'trigger-to-create2',
    inputs = { },
    duration = 'PT5S'
}))
sys.await_completed(req_one)

req_two = fail_on_error(sys.triggers.get(req_one.id))
assert(req_two.status == 'Active')

status_req = fail_on_error(sys.triggers.activate(req_two))
sys.await_completed(status_req)

req_two = fail_on_error(sys.triggers.get(req_one.id))
print(req_two.status)
assert(req_two.status == 'Active')
assert(req_two.type == 'FixedRate')
assert(req_two.name == 'trigger-to-create2')
assert(req_two.func.name == "test-func2")
assert(req_two.namespace.id == '539')
assert(req_two.namespace.name == "root-namespace")
assert(req_two.duration == "PT5S")