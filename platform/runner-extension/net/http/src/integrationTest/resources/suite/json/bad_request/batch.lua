local http = require_plugin('net.http')

local err, response = http.execute({
    http.requests.get({ url = '/v1/json-error' }),
    http.requests.post({ url = '/v1/json-error' }),
    http.requests.patch({ url = '/v1/json-error' }),
    http.requests.put({ url = '/v1/json-error' }),
    http.requests.delete({ url = '/v1/json-error' }),
})

assert(err == nil)
assert(response ~= nil)
assert(#response == 5)

for idx = 1, 5 do
    assert(response[idx].status_code == 400)
    assert(response[idx].content_type == 'application/json')

    assert(response[idx].content ~= nil)
    assert(table_length(response[idx].content) == 3)
    assert(response[idx].content.code == 400)
    assert(response[idx].content.message == 'bad-request')
    assert(response[idx].content['boolean-value'] == true)
end
