sys = require("sys")

-- HELPERS
function findCorr(list, correlation_id)
    for index, value in ipairs(list) do
        if value.correlation_id == correlation_id then
            return value
        end
    end
    return nil
end

function findFlow(list, name)
    for index, value in ipairs(list) do
        if value.name == name then
            return value
        end
    end
    return
end

--COLLECT EXECS


execs = fail_on_error(sys.execs.list())
exec_one = findCorr(execs, 'func-1-invoke')
exec_two = findCorr(execs, 'func-2-invoke')

assert(exec_one ~= nil)
assert(exec_two ~= nil)

flows = fail_on_error(sys.flows.list())
flow_one = findFlow(flows, 'flow-1')
flow_two = findFlow(flows, 'flow-2')

assert(flow_one ~= nil)
assert(flow_two ~= nil)

flow_one_execs = sys.flows.execs(flow_one.id)
--flow_two_execs = sys.flows.execs(flow_two.id)

--print(#flow_one_execs)
--assert(#flow_one_execs == 1)
--assert(#flow_two_execs == 1)