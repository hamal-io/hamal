sys = require_plugin('std.sys')

-- Remember: Each test is an adhoc invocation
err, execs = sys.execs.list()
assert(err == nil)
assert(#execs == 3)

assert(execs[1].status == 'Started')  -- <- current
assert(execs[2].status == 'Completed') -- <- function invoked from 001
assert(execs[3].status == 'Completed') -- <- 001 invocation
