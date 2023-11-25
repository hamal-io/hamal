sys = require('sys')

flow = fail_on_error(sys.flows.create({ name = 'flow-1' }))

-- function name is unique
func_one = fail_on_error(sys.funcs.create({ flow_id = '1', name = 'func-name' }))
sys.await_completed(func_one)
assert(func_one ~= nil)

func_one = fail_on_error(sys.funcs.create({ flow_id = '1', name = 'func-name' }))
assert(sys.await_failed(func_one) == nil)
assert(func_one ~= nil)

_, hooks = sys.funcs.list()
assert(#hooks == 1)

-- same name different flow
func_one = fail_on_error(sys.funcs.create({ flow_id = flow.id, name = 'func-name' }))
assert(err == nil)
sys.await_completed(func_one)

_, hooks = sys.funcs.list()
assert(#hooks == 2)