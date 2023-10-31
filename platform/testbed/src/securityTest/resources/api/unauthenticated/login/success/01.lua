-- As an unauthenticated user it must be possible to acquire a tokens
-- With the acquired token api requests are possible - like fetching pre-existing namespaces

http = require 'net.http'

err, res = http.post('/v1/anonymous-accounts', { json = { } })
assert(err == nil)

assert(res.status_code == 202)
assert(res.content_type == 'application/json')

content = res.content
assert(content.accountId ~= nil)
assert(content.token ~= nil)
assert(#content.groupIds == 1)

print(content.token)
print(content.groupIds[1])
print('/v1/groups/' .. content.groupIds[1] .. '/namespaces')

err, res = http.get('/v1/groups/' .. content.groupIds[1] .. '/namespaces', { headers = {
    ['authorization'] = 'Bearer ' .. content.token
} })

assert(res.err == nil)
assert(res.status_code == 200)

content = res.content
assert(#content.namespaces == 1)
