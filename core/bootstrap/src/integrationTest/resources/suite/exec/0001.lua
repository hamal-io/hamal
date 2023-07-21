local log = require('log')
local sys = require('sys')
local test = require('test')

-- Remember: Each test is an adhoc invoked exec
local execs = sys.execs.list()
test.assert(execs.length == 1)

test.complete()