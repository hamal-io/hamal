admin = require('sys')
--
-- Remember: Each test is an adhoc invoked exec
err, execs = admin.exec.list()
assert(err == nil)
assert(#execs == 1)