sys = require('sys')

namespace = fail_on_error(sys.namespace.create({ name = 'namespace-1' }))

-- function name is unique
func = fail_on_error(sys.func.create({ namespace_id = '1', name = 'func-name' }))
sys.await_completed(func)
assert(func ~= nil)

func = fail_on_error(sys.func.create({ namespace_id = '1', name = 'func-name' }))
assert(sys.await_failed(func) == nil)
assert(func ~= nil)

_, hooks = sys.func.list()
assert(#hooks == 1)

-- same name different namespace
func = fail_on_error(sys.func.create({ namespace_id = namespace.id, name = 'func-name' }))
assert(err == nil)
sys.await_completed(func)

_, hooks = sys.func.list()
assert(#hooks == 2)