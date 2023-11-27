sys = require_plugin('sys')

function findInList(list, key, matchValue)
    for index, value in ipairs(list) do
        if value[key] == matchValue then
            return value
        end
    end
    return nil
end

--COLLECT EXECS
execs = fail_on_error(sys.execs.list())

exec_one = findInList(execs, 'correlation_id', 'func-1-invoke')
exec_two = findInList(execs, 'correlation_id', 'func-2-invoke')

assert(exec_one ~= nil)
assert(exec_two ~= nil)

flows = fail_on_error(sys.flows.list())
flow_one = findInList(flows, 'name', 'flow-1')
flow_two = findInList(flows, 'name', 'flow-2')

assert(flow_one ~= nil)
assert(flow_two ~= nil)

flow_one_execs = fail_on_error(sys.flows.execs(flow_one.id))
flow_two_execs = fail_on_error(sys.flows.execs(flow_two.id))

assert(flow_one_execs ~= nil)
assert(flow_two_execs ~= nil)

--Execs per flow
assert(#flow_one_execs == 1)
assert(flow_one_execs[1].status == 'Completed')
assert(flow_one_execs[1].id == exec_one.id)

assert(#flow_two_execs == 1)
assert(flow_two_execs[1].status == 'Completed')
assert(flow_two_execs[1].id == exec_two.id)