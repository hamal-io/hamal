sys = require('sys')

while true do
    -- Remember: Each test is an adhoc invocation
    err, execs = sys.exec.list()
    assert(err == nil)
    assert(#execs == 3)

    assert(execs[1].status == 'Started')  -- <- current
    if execs[2].status == 'Failed' then
        break
    end
    assert(execs[3].status == 'Completed') -- <- 001 invocation
end

assert(execs[2].status == 'Failed') -- <- function invoked from 001
