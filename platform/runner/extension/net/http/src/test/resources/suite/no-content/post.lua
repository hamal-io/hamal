local http = require('net.http').create({
    base_url = context.env.test_url
})

local err, response = http.post({ url = "/v1/status?code=204" })
assert(err == nil)
assert(response ~= nil)
assert(response.content == nil)