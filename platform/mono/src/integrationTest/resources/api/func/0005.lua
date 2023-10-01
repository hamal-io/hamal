sys = require('sys')

_, namespace_req = sys.namespace.create({ name = 'namespace-1' })

-- function name is unique
err, func_req = sys.func.create({ name = 'func-name' })
sys.await_completed(func_req)
assert(err == nil)
assert(func_req ~= nil)

err, func_req = sys.func.create({ name = 'func-name' })
assert(sys.await_failed(func_req) == nil)
assert(err == nil)
assert(func_req ~= nil)

_, funcs = sys.func.list()
assert(#funcs == 1)

-- same name different namespace
err, func_req = sys.func.create({ name = 'func-name', namespace_id = namespace_req.id })
assert(err == nil)
sys.await_completed(func_req)

_, funcs = sys.func.list()
assert(#funcs == 2)
