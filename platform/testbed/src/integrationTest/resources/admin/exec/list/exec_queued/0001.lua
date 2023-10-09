sys = require('sys')
--

err, req = sys.adhoc({ inputs = {}, code = [[ answer = 42 -- but what was the question ?]] })
assert(err == nil)

-- Remember: Each test is an adhoc invoked exec
while true do
    err, execs = sys.exec.list()
    assert(err == nil)
    assert(#execs == 2)

    if execs[1].status == 'Queued' then
        break
    end

    assert(execs[2].status == 'Started') -- <- Current

end

assert(execs[1].id == req.id)
assert(execs[1].status == 'Queued')