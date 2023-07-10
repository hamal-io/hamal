local log = require('log')
local sys = require('sys')
local test = require('test')

-- Remember: Each test is an adhoc invoked exec
local execs = sys.exec.list()
test.assert(execs.length == 1)

local test_exec = execs[1]

local exec = sys.exec.get(test_exec.id)
test.assert(exec.id == test_exec.id)
test.assert(exec.status == 'Started')
test.assert(exec.correlationId == nil)
test.assert(exec.inputs == {})

test.complete()