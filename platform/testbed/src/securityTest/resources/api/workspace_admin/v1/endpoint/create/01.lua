-- As a workspace admin, I can create a endpoint in my own namespace

http = require('net.http').create({
    base_url = context.env.test_api
})

err, res = http.post({
    url = '/v1/namespaces/1/endpoints',
    headers = { Authorization = 'Bearer ' .. context.env.token },
    json = {
        name = 'created',
        funcId = '1',
        method = 'Get'
    }
})

assert(err == nil)
assert(res.status_code == 202)
assert(res.content_type == 'application/json;charset=UTF-8')
