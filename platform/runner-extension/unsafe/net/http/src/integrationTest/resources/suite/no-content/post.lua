local http = require('net.http')

local err, response = http.post("/v1/status?code=204")
assert(err == nil)
assert(response ~= nil)
assert(response.content == nil)