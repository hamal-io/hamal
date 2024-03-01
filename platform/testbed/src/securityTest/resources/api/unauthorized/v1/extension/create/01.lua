-- As a unauthorized user I can not create an extension in another user's workspace

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
assert(res.status_code == 404)
assert(res.content_type == 'application/json;charset=UTF-8')

assert(res.content.message == 'Workspace not found')
