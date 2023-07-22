--local sys = require('sys')
--local test = require('test')
--
--local func_id = sys.funcs.create({
--    name = 'empty-test-func',
--    inputs = {
--        hamal = 'rockz'
--    },
--    code = [[4 + 2]]
--})
--
--local func = sys.funcs.get(func_id)
--test.assert(func.id == func_id)
--test.assert(func.name == 'empty-test-func')
--test.assert(func.inputs == { hamal = 'rockz' })
--test.assert(func.code == [[4 + 2]])
--test.assert(func.length == 4)

test.complete()