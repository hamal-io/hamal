local http = require_plugin('net.http')

local url = context.env.test_url .. '/v1/json-empty-object'

local err, response = http.execute({
    http.requests.get({ url = url, produces = "JSON", consumes = "JSON" }),
    http.requests.post({ url = url, produces = "JSON", consumes = "JSON" }),
    http.requests.patch({ url = url, produces = "JSON", consumes = "JSON" }),
    http.requests.put({ url = url, produces = "JSON", consumes = "JSON" }),
    http.requests.delete({ url = url, produces = "JSON", consumes = "JSON" }),
})

assert(err == nil)
assert(response ~= nil)
assert(#response == 5)

for idx = 1, 5 do
    assert(response[idx].status_code == 200)
    assert(response[idx].content_type == 'application/json;charset=UTF-8')
    assert(response[idx].content ~= nil)
    assert(table_length(response[idx].content) == 0)
end
