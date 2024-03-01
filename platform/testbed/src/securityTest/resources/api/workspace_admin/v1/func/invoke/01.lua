-- As a workspace admin, I can invoke my own func

http = require('net.http').create({
    base_url = context.env.test_api
})

err, res = http.post({
    url = '/v1/funcs/1/invoke',
    headers = { Authorization = 'Bearer ' .. context.env.token }
})
assert(err == nil)
assert(res.status_code == 202)
assert(res.content_type == 'application/json;charset=UTF-8')