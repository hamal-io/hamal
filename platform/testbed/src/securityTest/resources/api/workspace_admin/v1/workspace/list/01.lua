-- As a workspace admin, I can list my own workspaces

http = require('net.http').create({
    base_url = context.env.test_api
})

err, res = http.get({
    url = '/v1/workspaces?ids=1',
    headers = { Authorization = 'Bearer ' .. context.env.token }
})
assert(err == nil)
assert(res.status_code == 200)
assert(res.content_type == 'application/json;charset=UTF-8')

workspaces = res.content.workspaces
assert(#workspaces == 1)
assert(workspaces[1].id == '1')