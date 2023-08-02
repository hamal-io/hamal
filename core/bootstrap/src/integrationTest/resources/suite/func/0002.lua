local sys = require('sys')
local test = require('test')

local err, funcs = sys.func.list()
assert(#funcs == 0)

print("DONE")

--local err, funcs = sys.func.list()
--assert(err == nil)
--assert(#funcs == 0)
--
--local err, func_one = sys.func.create({
--    name = 'func-1'
--})
--assert(err == nil)
--assert(err ~= nil)
--assert(func_one ~= nil)

--local err, funcs = sys.func.list()
--assert(#funcs == 1)

--local func_one = funcs[1]
--assert(func_one.id == func_one_id)
--assert(func_one.name == 'func-1')
--assert(func_one.length == 2)
--
--local func_two_id = sys.funcs.create({ })
--
--funcs = sys.funcs.list()
--assert(funcs.length == 2)
--
--local func_two = funcs[1]
--assert(func_two.id == func_two_id)
--assert(func_two.name == '')
--
--func_one = funcs[2]
--assert(func_one.id == func_one_id)
--assert(func_one.name == 'func-1')
--
test.complete()