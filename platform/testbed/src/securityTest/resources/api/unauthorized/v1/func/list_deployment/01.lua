-- As an unauthenticated user, I can not list deployments of another user's func

http = require('net.http').create({
    base_url = context.env.test_api
})

err, res = http.get({
    url = '/v1/funcs/1/deployments',
    headers = { Authorization = 'Bearer ' .. context.env.token }
})
assert(err == nil)
assert(res.status_code == 404)
assert(res.content_type == 'application/json;charset=UTF-8')
assert(res.content.message == 'Func not found')


