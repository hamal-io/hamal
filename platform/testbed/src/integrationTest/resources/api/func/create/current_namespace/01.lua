sys = require('sys')

err, func = sys.func.create({
    name = 'test-func',
    inputs = {},
    code = [[4 + 2]]
})
assert(err == nil)
sys.await_completed(func)

assert(func.id ~= nil)
assert(func.status == 'Submitted')
assert(func.func_id ~= nil)
assert(func.group_id == '1')
assert(func.namespace_id == '1')

err, func = sys.func.get(func.id)
assert(err == nil)

assert(func.namespace.id == '1')
assert(func.namespace.name == 'root-namespace')
assert(func.name == 'test-func')
assert(func.code == [[4 + 2]])
assert(func.code_id ~= nil)
assert(func.code_version == 1)