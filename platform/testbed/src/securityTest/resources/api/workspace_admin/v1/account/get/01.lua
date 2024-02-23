-- As a workspace admin, I can access my own account

http = require('net.http').create({
    base_url = context.env.test_api
})

err, res = http.get({
    url = '/v1/accounts/1',
    headers = { Authorization = 'Bearer ' .. context.env.token }
})
assert(err == nil)
assert(res.status_code == 200)
assert(res.content_type == 'application/json;charset=UTF-8')

account = res.content
assert(account.id == '1')