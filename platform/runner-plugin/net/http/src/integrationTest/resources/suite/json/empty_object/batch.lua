local http = require_plugin('net.http')

local err, response = http.execute({
    http.requests.get({ url = '/v1/json-empty-object' }),
    http.requests.post({ url = '/v1/json-empty-object' }),
    http.requests.patch({ url = '/v1/json-empty-object' }),
    http.requests.put({ url = '/v1/json-empty-object' }),
    http.requests.delete({ url = '/v1/json-empty-object' }),
})

assert(err == nil)
assert(response ~= nil)
assert(#response == 5)

for idx = 1, 5 do
    assert(response[idx].status_code == 200)
    assert(response[idx].content_type == 'application/json')
    assert(response[idx].content ~= nil)
    assert(table_length(response[idx].content) == 0)
end
