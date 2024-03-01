-- As a workspace admin, I can invoke adhoc in my namespace

http = require('net.http').create({
    base_url = context.env.test_api
})

err, res = http.post({
    url = '/v1/namespaces/1/adhoc',
    headers = { Authorization = 'Bearer ' .. context.env.token },
    json = {
        code = 'print("hello hamal")'
    }
})
assert(err == nil)
assert(res.status_code == 202)
assert(res.content_type == 'application/json;charset=UTF-8')