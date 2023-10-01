admin = require('sys')

_, namespace_req = admin.namespace.create({ name = 'namespace-1' })

-- function name is unique
err, func_req = admin.func.create({ name = 'func-name' })
admin.await_completed(func_req)
assert(err == nil)
assert(func_req ~= nil)

err, func_req = admin.func.create({ name = 'func-name' })
assert(admin.await_failed(func_req) == nil)
assert(err == nil)
assert(func_req ~= nil)

_, funcs = admin.func.list()
assert(#funcs == 1)

-- same name different namespace
err, func_req = admin.func.create({ name = 'func-name', namespace_id = namespace_req.id })
assert(err == nil)
admin.await_completed(func_req)

_, funcs = admin.func.list()
assert(#funcs == 2)
