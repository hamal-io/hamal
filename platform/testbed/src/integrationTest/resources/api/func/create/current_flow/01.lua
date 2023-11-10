sys = require('sys')

err, func = sys.funcs.create({
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
assert(func.flow_id == '1')

err, func = sys.funcs.get(func.id)
assert(err == nil)

assert(func.flow.id == '1')
assert(func.flow.name == 'root-flow')
assert(func.name == 'test-func')

assert(func.code.id ~= nil)
assert(func.code.current.version == 1)
assert(func.code.current.value == [[4 + 2]])
assert(func.code.deployed.version == 1)
assert(func.code.deployed.value == [[4 + 2]])