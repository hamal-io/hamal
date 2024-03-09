-- Logging out invalidates a token

http = require('net.http').create({
    base_url = context.env.test_api
})

err, res = http.post({
    url = '/v1/anonymous-accounts',
    json = { }
})
assert(err == nil)

assert(res.status_code == 202)
assert(res.content_type == 'application/json;charset=UTF-8')

content = res.content
assert(content.requestId ~= nil)
assert(content.id ~= nil)
assert(content.token ~= nil)
assert(#content.workspaceIds == 1)

token = content.token

err, res = http.get({
    url = '/v1/workspaces/' .. content.workspaceIds[1] .. '/namespaces',
    headers = {
        ['authorization'] = 'Bearer ' .. token
    }
})

assert(res.err == nil)
assert(res.status_code == 200)

err, res = http.post({
    url = '/v1/logout',
    headers = {
        ['authorization'] = 'Bearer ' .. token
    }
})

assert(res.err == nil)
assert(res.status_code == 204)

err, res = http.get({
    url = '/v1/workspaces/' .. content.workspaceIds[1] .. '/namespaces',
    headers = {
        ['authorization'] = 'Bearer ' .. token
    }
})

assert(res.err == nil)
assert(res.status_code == 403)
assert(res.content['message'] == 'That\'s an error')