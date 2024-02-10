sys = require_plugin 'sys'
http = require('net.http').create()

namespace = fail_on_error(sys.namespaces.create({ name = 'io::hamal' }))
sys.await_completed(namespace)

func_one = fail_on_error(sys.funcs.create({
    namespace_id = namespace.namespace_id,
    name = 'test-func',
    inputs = {},
    code = [[
        context.complete({answer = 42})
    ]]
}))
sys.await_completed(func_one)

submitted_endpoint = fail_on_error(sys.endpoints.create({
    namespace_id = namespace.namespace_id,
    func_id = func_one.func_id,
    name = 'test-endpoint'
}))

sys.await_completed(submitted_endpoint)

err, res = http.patch({
    url = context.env.api_host .. '/v1/endpoints/' .. submitted_endpoint.endpoint_id .. '/invoke',
    headers = {
        ['authorization'] = 'Bearer root-token'
    }
})

content = res.content

assert(content.id ~= nil)
assert(content.status == 'Completed')

assert(content.invocation.method == 'Patch')

assert(content.result.answer == 42.0)
