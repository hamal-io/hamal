-- As a workspace admin, I can list my own exec logs

http = require('net.http').create({
    base_url = context.env.test_api
})

http = require('net.http').create({
    base_url = context.env.test_api
})

err, res = http.get({
    url = '/v1/execs/1/logs',
    headers = { Authorization = 'Bearer ' .. context.env.token }
})
assert(err == nil)
assert(res.status_code == 200)
assert(res.content_type == 'application/json;charset=UTF-8')
assert(#res.content.logs == 0)
