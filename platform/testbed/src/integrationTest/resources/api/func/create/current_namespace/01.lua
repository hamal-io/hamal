sys = require_plugin('sys')

err, func_one = sys.funcs.create({
    name = 'test-func',
    inputs = {},
    code = [[4 + 2]]
})
assert(err == nil)
sys.await_completed(func_one)

assert(func_one.id ~= nil)
assert(func_one.status == 'Submitted')
assert(func_one.func_id ~= nil)
assert(func_one.workspace_id == '539')
assert(func_one.namespace_id == '539')

err, func_one = sys.funcs.get(func_one.id)
assert(err == nil)

assert(func_one.namespace.id == '539')
assert(func_one.namespace.name == 'root-namespace')
assert(func_one.name == 'test-func')

assert(func_one.code.id ~= nil)
assert(func_one.code.version == 1)
assert(func_one.code.value == [[4 + 2]])

assert(func_one.deployment.version == 1)
assert(func_one.deployment.value == [[4 + 2]])