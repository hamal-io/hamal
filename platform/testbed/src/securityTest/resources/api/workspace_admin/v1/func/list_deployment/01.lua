-- As a workspace admin, I can list my own func deployments
http = require('net.http').create({
    base_url = context.env.test_api
})

func_req = fail_on_error(http.post({
    url = '/v1/namespaces/1/funcs',
    headers = { Authorization = 'Bearer ' .. context.env.token },
    json = {
        name = 'function-for-deployment',
        inputs = {},
        code = 'print("hamal rocks")',
        codeType = 'Lua54'
    }
})).content

http.post({
    url = '/v1/funcs/' .. func_req.id .. '/deploy',
    headers = { Authorization = 'Bearer ' .. context.env.token },
    json = { version = '1', message = 'deploy message' }
})

err, res = http.get({
    url = '/v1/funcs/' .. func_req.id .. '/deployments',
    headers = { Authorization = 'Bearer ' .. context.env.token }
})

assert(err == nil)
assert(res.status_code == 200)
assert(res.content_type == 'application/json;charset=UTF-8')

assert(#res.content.deployments == 1)

