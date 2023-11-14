local http = require('net.http')

local err, response = http.execute({
    http.requests.get({ url = "/v1/status?code=204" }),
    http.requests.post({ url = "/v1/status?code=204" }),
    http.requests.patch({ url = "/v1/status?code=204" }),
    http.requests.put({ url = "/v1/status?code=204" }),
    http.requests.delete({ url = "/v1/status?code=204" }),
})

assert(err == nil)
assert(response ~= nil)
assert(#response == 5)

for idx = 1, 5 do
    assert(response[idx].content == nil)
end
