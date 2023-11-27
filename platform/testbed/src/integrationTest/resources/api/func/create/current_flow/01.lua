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
assert(func_one.group_id == '1')
assert(func_one.flow_id == '1')

err, func_one = sys.funcs.get(func_one.id)
assert(err == nil)

assert(func_one.flow.id == '1')
assert(func_one.flow.name == 'root-flow')
assert(func_one.name == 'test-func')

assert(func_one.code.id ~= nil)
assert(func_one.code.current.version == 1)
assert(func_one.code.current.value == [[4 + 2]])
assert(func_one.code.deployed.version == 1)
assert(func_one.code.deployed.value == [[4 + 2]])