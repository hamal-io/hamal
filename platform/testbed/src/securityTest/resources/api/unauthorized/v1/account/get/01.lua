-- As a unauthorized user I can not access another user account

http = require('net.http').create({
    base_url = context.env.test_api
})

err, res = http.get({
    url = '/v1/accounts/1',
    headers = { Authorization = 'Bearer ' .. context.env.token }
})
assert(err == nil)
assert(res.status_code == 404)
assert(res.content_type == 'application/json;charset=UTF-8')
assert(res.content.message == 'Account not found')
