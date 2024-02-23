-- As a unauthorized user I can not access a exec of another user

http = require('net.http').create({
    base_url = context.env.test_api
})

err, res = http.get({
    url = '/v1/execs/1',
    headers = { Authorization = 'Bearer ' .. context.env.token }
})
assert(err == nil)
assert(res.status_code == 404)
assert(res.content_type == 'application/json;charset=UTF-8')
assert(res.content.message == 'Exec not found')
