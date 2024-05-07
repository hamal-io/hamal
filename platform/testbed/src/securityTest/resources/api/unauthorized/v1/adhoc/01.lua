-- As an unauthorized user, I can not invoke adhoc in another user's namespace

http = require('net.http').create({
    base_url = context.env.test_api
})

err, res = http.post({
    url = '/v1/namespaces/1/adhoc',
    headers = { Authorization = 'Bearer ' .. context.env.token },
    body =  {
        code = 'print("hello hamal")'
    }
})
assert(err == nil)
assert(res.status_code == 404)
assert(res.content_type == 'application/json;charset=UTF-8')

assert(res.content.message == 'Namespace not found')