local sys = require('sys')
local test = require('test')
--
-- Remember: Each test is an adhoc invoked exec
local err, execs = sys.exec.list()
assert(err == nil)
assert(#execs == 2)

test.complete()