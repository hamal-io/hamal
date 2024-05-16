sys = require('std.sys').create({
    base_url = context.env.api_host
})

func_request = fail_on_error(sys.func.create({
    name = 'test-func',
    inputs = {},
    code = [[4 + 2]]
}))
sys.await_completed(func_request)

assert(func_request.id ~= nil)
assert(func_request.requestStatus == 'Submitted')
assert(func_request.workspaceId == '539')
assert(func_request.namespaceId == '539')

func_one = fail_on_error(sys.func.get(func_request.id))

assert(func_one.namespace.id == '539')
assert(func_one.namespace.name == 'root-namespace')
assert(func_one.name == 'test-func')

assert(func_one.code.id ~= nil)
assert(func_one.code.version == 1)
assert(func_one.code.value == [[4 + 2]])

assert(func_one.deployment.version == 1)
assert(func_one.deployment.value == [[4 + 2]])