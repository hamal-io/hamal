-- As a workspace admin, I can access my own func
http = require('net.http').create({
    base_url = context.env.test_api
})

err, res = http.get({
    url = '/v1/funcs/1',
    headers = { Authorization = 'Bearer ' .. context.env.token }
})
assert(err == nil)
assert(res.status_code == 200)
assert(res.content_type == 'application/json;charset=UTF-8')

func = res.content
assert(func.id == '1')
assert(func.code.id == '1')
assert(func.code.version == 1)
assert(func.name == '1-func')
assert(func.class == 'ApiFunc')