local sys = require('sys')

local err, funcs = sys.func.list()
assert(err == nil)
assert(#funcs == 0)

local err, func_one = sys.func.create({
    name = 'func-1'
})
assert(err == nil)
assert(func_one ~= nil)
--
local _, funcs = sys.func.list()
assert(#funcs == 1)

assert(func_one.id == funcs[1].id)
assert(funcs[1].name == 'func-1')

sys.func.create({})

_, funcs = sys.func.list()
assert(#funcs == 2)
