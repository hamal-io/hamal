-- As a workspace admin, I can list my own funcs

http = require('net.http').create({
    base_url = context.env.test_api
})

err, res = http.get({
    url = '/v1/funcs?namespace_ids=1&ids=1',
    headers = { Authorization = 'Bearer ' .. context.env.token }
})
assert(err == nil)
assert(res.status_code == 200)
assert(res.content_type == 'application/json;charset=UTF-8')
assert(#res.content.funcs == 1)

err, res = http.get({
    url = '/v1/namespaces/1/funcs?ids=1',
    headers = { Authorization = 'Bearer ' .. context.env.token }
})
assert(err == nil)
assert(res.status_code == 200)
assert(res.content_type == 'application/json;charset=UTF-8')
assert(#res.content.funcs == 1)
