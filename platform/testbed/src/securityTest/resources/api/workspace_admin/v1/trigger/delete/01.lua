-- As a workspace admin, I can delete my own trigger


http = require('net.http').create({
    base_url = context.env.test_api
})

err, res = http.delete({
    url = '/v1/triggers/5',
    headers = { Authorization = 'Bearer ' .. context.env.token },
})

assert(err == nil)
assert(res.status_code == 202)
assert(res.content_type == 'application/json;charset=UTF-8')