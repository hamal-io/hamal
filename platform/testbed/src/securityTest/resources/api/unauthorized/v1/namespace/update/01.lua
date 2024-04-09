http = require('net.http').create({
    base_url = context.env.test_api
})

err, res = http.patch({
    url = '/v1/namespaces/1',
    headers = { Authorization = 'Bearer ' .. context.env.token },
    json = { name = 'updated' }
})

assert(err == nil)
assert(res.status_code == 404)
assert(res.content_type == 'application/json;charset=UTF-8')
assert(res.content.message == 'Namespace not found')