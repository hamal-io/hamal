-- As an unauthenticated user it must be possible to acquire a token by creating an anonymous account
-- With the acquired token api requests are possible - like fetching flows

http = require('net.http').create({
    base_url = context.env.test_api
})

err, res = http.post({ url = '/v1/anonymous-accounts', json = { } })
assert(err == nil)

assert(res.status_code == 202)
assert(res.content_type == 'application/json')

content = res.content
assert(content.id ~= nil)
assert(content.accountId ~= nil)
assert(content.token ~= nil)
assert(#content.groupIds == 1)

for _ = 1, 10 do
    err, res = http.get({
        url = '/v1/groups/' .. content.groupIds[1] .. '/flows',
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
assert(#content.flows == 1)
