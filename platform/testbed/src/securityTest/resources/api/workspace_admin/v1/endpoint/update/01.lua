-- As a workspace admin, I can update my own endpoints

http = require('net.http').create({
    base_url = context.env.test_api
})

err, res = http.patch({
    url = '/v1/endpoints/1',
    headers = { Authorization = 'Bearer ' .. context.env.token },
    json = { name = 'updated' }
})

assert(err == nil)
assert(res.status_code == 202)
assert(res.content_type == 'application/json;charset=UTF-8')
