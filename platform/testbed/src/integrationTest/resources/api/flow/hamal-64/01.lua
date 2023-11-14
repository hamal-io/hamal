--hamal-64

sys = require("sys")

-- CREATE FLOWS
local flow_one_req = fail_on_error(sys.flows.create({
    name = 'flow-501'
}))
sys.await_completed(flow_one_req)

local flow_two_req = fail_on_error(sys.flows.create({
    name = 'flow-502'
}))
sys.await_completed(flow_two_req)

-- CREATE FUNCS
local func_one_req = fail_on_error(sys.funcs.create({
    flow_id = flow_one_req.flow_id,
    name = 'test-func1',
    code = '1 + 2'
}))
sys.await_completed(func_one_req)

local func_two_req = fail_on_error(sys.funcs.create({
    flow_id = flow_two_req.flow_id,
    name = 'test-func2',
    code = '3 + 4'
}))
sys.await_completed(func_two_req)


-- INVOKE FUNCS
--Remember: Each test is an adhoc invocation
local invoke_one_req = fail_on_error(sys.funcs.invoke({
    id = func_one_req.func_id,
    correlation_id = 'fuck-1',
    inputs = { }
}))
sys.await_completed(invoke_one_req)

local invoke_two_req = fail_on_error(sys.funcs.invoke({
    id = func_two_req.func_id,
    correlation_id = 'fuck-2',
    inputs = { }
}))
sys.await_completed(invoke_two_req)
execs = fail_on_error(sys.execs.list())



--COLLECT EXECS
function searchList(list, correlation_id)
    for index, value in ipairs(list) do
        if value.correlation_id == correlation_id then
            return index  -- returns the index of the found item
        end
    end
    return nil  -- item not found
end

my_exec_one = nil
my_exec_two = nil

execs = fail_on_error(sys.execs.list())
my_exec_one = searchList(execs, 'fuck-1')
my_exec_two = searchList(execs, 'fuck-2')

assert(my_exec_one ~= nil)
assert(my_exec_two ~= nil)

--assert(sys.flows.execs(flow_one_req.flow_id) ~= nil)







