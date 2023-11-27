sys = require_plugin 'sys'
http = require_plugin 'net.http'

flow = fail_on_error(sys.flows.create({ name = 'io::hamal' }))
sys.await_completed(flow)

func_one = fail_on_error(sys.funcs.create({
    flow_id = flow.flow_id,
    name = 'test-func',
    inputs = {},
    code = [[
        context.complete({answer = 42})
    ]]
}))
sys.await_completed(func_one)

submitted_endpoint = fail_on_error(sys.endpoints.create({
    flow_id = flow.flow_id,
    func_id = func_one.func_id,
    name = 'test-endpoint'
}))

sys.await_completed(submitted_endpoint)

err, res = http.put({
    url = context.api.host .. '/v1/endpoints/' .. submitted_endpoint.endpoint_id .. '/invoke',
    headers = {
        ['authorization'] = 'Bearer root-token'
    }
})

content = res.content

assert(content.id ~= nil)
assert(content.status == 'Completed')

assert(content.invocation.method == 'Put')

assert(content.result.value.answer.type == 'NumberType')
assert(content.result.value.answer.value == '42')

