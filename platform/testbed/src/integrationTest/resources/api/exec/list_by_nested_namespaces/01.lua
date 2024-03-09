sys = require_plugin('sys')

local namespace_one_req = fail_on_error(sys.namespaces.append({
    name = 'namespace-1'
}))
sys.await_completed(namespace_one_req)

local namespace_two_req = fail_on_error(sys.namespaces.append({
    name = 'namespace-2'
}))
sys.await_completed(namespace_two_req)

local func_one_req = fail_on_error(sys.funcs.create({
    namespace_id = namespace_one_req.id,
    name = 'func-1',
    code = [[print(hamal)]]
}))
sys.await_completed(func_one_req)

local func_two_req = fail_on_error(sys.funcs.create({
    namespace_id = namespace_two_req.id,
    name = 'func-2',
    code = [[print(lamah)]]
}))
sys.await_completed(func_two_req)

local invoke_one_req = fail_on_error(sys.funcs.invoke({
    id = func_one_req.id,
    correlation_id = 'func-1-invoke',
    inputs = { }
}))
sys.await_completed(invoke_one_req)

local invoke_two_req = fail_on_error(sys.funcs.invoke({
    id = func_two_req.id,
    correlation_id = 'func-2-invoke',
    inputs = { }
}))
sys.await_completed(invoke_two_req)

