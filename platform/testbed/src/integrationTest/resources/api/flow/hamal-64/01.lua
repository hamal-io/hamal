--hamal-64

sys = require("sys")

-- CREATE FLOWS
local flow_one_req = fail_on_error(sys.flows.create({
    name = 'flow-1'
}))
sys.await_completed(flow_one_req)

local flow_two_req = fail_on_error(sys.flows.create({
    name = 'flow-2'
}))
sys.await_completed(flow_two_req)

-- CREATE FUNCS
local func_one_req = fail_on_error(sys.funcs.create({
    flow_id = flow_one_req.flow_id,
    name = 'func-1',
    code = [[print(hamal)]]
}))
sys.await_completed(func_one_req)

local func_two_req = fail_on_error(sys.funcs.create({
    flow_id = flow_two_req.flow_id,
    name = 'func-2',
    code = [[print(lamah)]]
}))
sys.await_completed(func_two_req)


-- INVOKE FUNCS
--Remember: Each test is an adhoc invocation
local invoke_one_req = fail_on_error(sys.funcs.invoke({
    id = func_one_req.func_id,
    correlation_id = 'func-1-invoke',
    inputs = { }
}))
sys.await_completed(invoke_one_req)

local invoke_two_req = fail_on_error(sys.funcs.invoke({
    id = func_two_req.func_id,
    correlation_id = 'func-2-invoke',
    inputs = { }
}))
sys.await_completed(invoke_two_req)












