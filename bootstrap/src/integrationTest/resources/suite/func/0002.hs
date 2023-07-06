local log = require('log')
local sys = require('sys')
local test = require('test')

local funcs = sys.func.list()
test.assert(funcs.length == 0)

local func_one_id = sys.func.create({
    name = 'func-1'
})

funcs = sys.func.list()
log.info(funcs.length == 1)

local func_one = funcs[1]
test.assert(func_one.id == func_one_id)
test.assert(func_one.name == 'func-1')

local func_two_id = sys.func.create({ })

funcs = sys.func.list()
test.assert(funcs.length == 2)

func_two = funcs[1]
test.assert(func_two.id == func_two_id)
test.assert(func_two.name == '')

func_one = funcs[2]
test.assert(func_one.id == func_one_id)
test.assert(func_one.name == 'func-1')

test.complete()