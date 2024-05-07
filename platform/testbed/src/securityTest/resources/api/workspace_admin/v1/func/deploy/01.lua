-- As a workspace admin, I can deploy my own funcs

http = require('net.http').create({
    base_url = context.env.test_api
})

err, res = http.post({
    url = '/v1/funcs/1/deploy',
    headers = { Authorization = 'Bearer ' .. context.env.token },
    body = { version = 1, message = 'deploy message' }
})

assert(err == nil)
assert(res.status_code == 202)
assert(res.content_type == 'application/json;charset=UTF-8')
