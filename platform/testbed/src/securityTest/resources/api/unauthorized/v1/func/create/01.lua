-- As a unauthorized user I can not create a func in another user's namespace

http = require('net.http').create({
    base_url = context.env.test_api
})

err, res = http.post({
    url = '/v1/namespaces/1/funcs',
    headers = { Authorization = 'Bearer ' .. context.env.token },
    json = {
        name = 'created',
        inputs = {},
        code = 'print("hamal rocks")',
        codeType = 'Lua54'
    }
})

assert(err == nil)
assert(res.status_code == 404)
assert(res.content_type == 'application/json;charset=UTF-8')
assert(res.content.message == 'Namespace not found')
