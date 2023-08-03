local sys = require('sys')
local test = require('test')
--
---- Remember: Each test is an adhoc invoked exec
local err, execs = sys.exec.list()
assert(err == nil)
assert(#execs == 1)

local test_exec = execs[1]

local err, exec = sys.exec.get(test_exec.id)
assert(err == nil)

assert(exec.id == test_exec.id)
assert(exec.status == 'Started')
assert(exec.correlationId == nil)
assert(#exec.inputs == 0)

test.complete()