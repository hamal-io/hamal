local log = require('log')
local sys = require('sys')
local test = require('test')

local funcs = sys.funcs.list()
test.assert(funcs.length == 0)

local func_one_id = sys.funcs.create({
    name = 'func-1'
})

funcs = sys.funcs.list()
log.info(funcs.length == 1)

local func_one = funcs[1]
test.assert(func_one.id == func_one_id)
test.assert(func_one.name == 'func-1')
test.assert(func_one.length == 2)

local func_two_id = sys.funcs.create({ })

funcs = sys.funcs.list()
test.assert(funcs.length == 2)

local func_two = funcs[1]
test.assert(func_two.id == func_two_id)
test.assert(func_two.name == '')

func_one = funcs[2]
test.assert(func_one.id == func_one_id)
test.assert(func_one.name == 'func-1')

test.complete()