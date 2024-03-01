-- As a workspace admin, I can create an extension  in my own workspace

http = require('net.http').create({
    base_url = context.env.test_api
})

err, res = http.post({
    url = '/v1/workspaces/1/extensions',
    headers = { Authorization = 'Bearer ' .. context.env.token },
    json = {
        name = 'created',
        code = 'print("Hello extensions")'
    }
})

assert(err == nil)
assert(res.status_code == 202)
assert(res.content_type == 'application/json;charset=UTF-8')
