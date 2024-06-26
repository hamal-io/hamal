-- As an unauthenticated user it must be possible to acquire a token by creating an anonymous account
-- With the acquired token api requests are possible - like fetching namespaces

http = require('net.http').create({
    base_url = context.env.test_api
})

err, res = http.post({ url = '/v1/anonymous-accounts', body =  { } })
assert(err == nil)

assert(res.status_code == 202)
assert(res.content_type == 'application/json;charset=UTF-8')

content = res.content
assert(content.requestId ~= nil)
assert(content.id ~= nil)
assert(content.token ~= nil)
assert(#content.workspaceIds == 1)

for _ = 1, 10 do
    err, res = http.get({
        url = '/v1/workspaces/' .. content.workspaceIds[1] .. '/namespaces',
        headers = {
            ['authorization'] = 'Bearer ' .. content.token
        } })
    if err == nil then
        break
    end
end

assert(res.err == nil)
assert(res.status_code == 200)

content = res.content
assert(#content.namespaces == 1)
