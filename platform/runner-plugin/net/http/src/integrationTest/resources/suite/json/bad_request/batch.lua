local http = require_plugin('net.http')

local url = context.env.test_url .. '/v1/json-error'

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
    assert(response[idx].status_code == 400)
    assert(response[idx].content_type == 'application/json')

    assert(response[idx].content ~= nil)
    assert(table_length(response[idx].content) == 3)
    assert(response[idx].content.code == 400)
    assert(response[idx].content.message == 'bad-request')
    assert(response[idx].content['boolean-value'] == true)
end
