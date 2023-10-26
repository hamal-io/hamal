sys = require('sys')

err, funcs = sys.func.list()
assert(err == nil)
assert(#funcs == 0)

func_one_req = fail_on_error(sys.func.create({ namespace_id = '1', name = 'func-1' }))
sys.await_completed(func_one_req)

assert(err == nil)
assert(func_one_req ~= nil)
--
_, funcs = sys.func.list()
assert(#funcs == 1)

assert(func_one_req.id == funcs[1].id)
assert(funcs[1].name == 'func-1')

func_two = fail_on_error(sys.func.create({ namespace_id = '1', name = 'func-2' }))
sys.await_completed(func_two)

_, funcs = sys.func.list()
assert(#funcs == 2)
