local http = require_plugin('net.http')

local err, response = http.put({ url = "/v1/status?code=204" })
assert(err == nil)
assert(response ~= nil)
assert(response.content == nil)