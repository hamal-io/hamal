sys = require_plugin('std.sys')
--
-- Remember: Each test is an adhoc invoked exec
err, execs = sys.execs.list()
assert(err == nil)
assert(#execs == 1)

assert(execs[1].status == 'Started')