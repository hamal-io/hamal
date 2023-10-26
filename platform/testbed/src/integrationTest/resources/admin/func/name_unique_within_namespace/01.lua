sys = require('sys')

namespace_req = fail_on_error(sys.namespace.create({ name = 'namespace-1' }))

-- function name is unique
func_req = fail_on_error(sys.func.create({ namespace_id = '1', name = 'func-name' }))
sys.await_completed(func_req)
assert(func_req ~= nil)

func_req = fail_on_error(sys.func.create({ namespace_id = '1', name = 'func-name' }))
assert(sys.await_failed(func_req) == nil)
assert(func_req ~= nil)

_, funcs = sys.func.list()
assert(#funcs == 1)

-- same name different namespace
func_req = fail_on_error(sys.func.create({ namespace_id = namespace_req.id, name = 'func-name' }))
assert(err == nil)
sys.await_completed(func_req)

_, funcs = sys.func.list()
assert(#funcs == 2)