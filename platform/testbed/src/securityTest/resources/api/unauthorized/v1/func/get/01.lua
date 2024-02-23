-- As a unauthorized user I can not access a func of another user
http = require('net.http').create({
    base_url = context.env.test_api
})

err, res = http.get({
    url = '/v1/funcs/1',
    headers = { Authorization = 'Bearer ' .. context.env.token }
})
assert(err == nil)
assert(res.status_code == 404)
assert(res.content_type == 'application/json;charset=UTF-8')
assert(res.content.message == 'Func not found')
