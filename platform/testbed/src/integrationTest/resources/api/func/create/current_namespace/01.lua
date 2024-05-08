sys = require('std.sys').create({
    base_url = context.env.api_host
})

err, func_one = sys.func.create({
    name = 'test-func',
    inputs = {},
    code = [[4 + 2]]
})
--assert(err == nil)

--assert(func_one.id ~= nil)
--assert(func_one.requestStatus == 'Submitted')
--assert(func_one.id ~= nil)
--assert(func_one.workspaceId == '539')
--assert(func_one.namespaceId == '539')
--
--err, func_one = sys.func.get(func_one.id)
--assert(err == nil)
--
--assert(func_one.namespace.id == '539')
--assert(func_one.namespace.name == 'root-namespace')
--assert(func_one.name == 'test-func')
--
--assert(func_one.code.id ~= nil)
--assert(func_one.code.version == 1)
--assert(func_one.code.value == [[4 + 2]])
--
--assert(func_one.deployment.version == 1)
--assert(func_one.deployment.value == [[4 + 2]])