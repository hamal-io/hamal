local log = require('log')
local sys = require('sys')
local test = require('test')

-- Remember: Each test is an adhoc invocation
local exec_id = sys.adhoc({
    inputs = {},
    code = [[
        1 + 1
    ]]
})
log.info(exec_id)

local execs = sys.exec.list()
log.info(execs)
test.assert(execs.length == 2)

local exec = execs[1]
test.assert(exec.id == exec_id)

test.complete()