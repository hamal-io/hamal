-- As a unauthorized user I can not list namespaces of another user

http = require('net.http').create({
    base_url = context.env.test_api
})

err, res = http.get({
    url = '/v1/workspaces/1/namespaces',
    headers = { Authorization = 'Bearer ' .. context.env.token }
})
assert(err == nil)
assert(res.status_code == 200)
assert(res.content_type == 'application/json;charset=UTF-8')
assert(#res.content.namespaces == 0)
