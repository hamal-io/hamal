sys = require('sys')

-- Remember: Each test is an adhoc invocation
execs = fail_on_error(sys.exec.list())
assert(err == nil)
assert(#execs == 3)

assert(execs[1].status == 'Started')    -- <- current
assert(execs[2].status == 'Failed')     -- <- function invoked from 001
assert(execs[3].status == 'Completed')  -- <- 001 invocation
