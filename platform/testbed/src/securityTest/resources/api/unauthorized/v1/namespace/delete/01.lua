-- As a unauthorized user I can not delete a namespace of another user
http = require('net.http').create({
    base_url = context.env.test_api
})

err, res = http.delete({
    url = '/v1/namespaces/1',
    headers = { Authorization = 'Bearer ' .. context.env.token },
    json = { name = 'delete' }
})

assert(err == nil)
assert(res.status_code == 404)
assert(res.content_type == 'application/json;charset=UTF-8')
assert(res.content.message == 'Namespace not found')