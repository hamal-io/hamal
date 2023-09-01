local sys = require('sys')
--
-- Remember: Each test is an adhoc invoked exec
local err, execs = sys.exec.list()
assert(err == nil)
assert(#execs == 1)