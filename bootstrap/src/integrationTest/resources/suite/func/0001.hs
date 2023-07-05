local log = require('log')
local sys = require('sys')
local test = require('test')

local func_id = sys.func.create({
    name = 'empty-test-func',
    inputs = {
        hamal = 'rockz'
    },
    code = '4 + 2'
})

log.info(func_id)

local func = sys.func.get(func_id)
log.info(func)

test.assert(func.id == func_id)
test.assert(func.name == 'empty-test-func')

test.complete()