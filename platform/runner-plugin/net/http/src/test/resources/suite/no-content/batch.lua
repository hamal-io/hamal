local http = require_plugin('net.http')

local url = context.env.test_url .. '/v1/status?code=204'

local err, response = http.execute({
    http.requests.get({ url = url }),
    http.requests.post({ url = url }),
    http.requests.patch({ url = url }),
    http.requests.put({ url = url }),
    http.requests.delete({ url = url }),
})

assert(err == nil)
assert(response ~= nil)
assert(#response == 5)

for idx = 1, 5 do
    assert(response[idx].content == nil)
end
