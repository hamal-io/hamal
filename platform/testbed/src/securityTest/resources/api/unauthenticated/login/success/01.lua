-- As an unauthenticated user it must be possible to acquire a token
-- With the acquired token api requests are possible - like fetching namespaces

http = require 'net.http'

err, res = http.post('/v1/anonymous-accounts', { json = { } })
assert(err == nil)

assert(res.status_code == 202)
assert(res.content_type == 'application/json')

content = res.content
assert(content.id ~= nil)
assert(content.accountId ~= nil)
assert(content.token ~= nil)
assert(#content.groupIds == 1)

for i = 1, 10 do
    err, res = http.get('/v1/groups/' .. content.groupIds[1] .. '/namespaces', { headers = {
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
