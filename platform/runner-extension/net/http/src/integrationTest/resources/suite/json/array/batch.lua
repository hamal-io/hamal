http = require('net.http').create({
    base_url = context.env.test_url
})

local err, response = http.execute({
    http.requests.get({ url = '/v1/json-array' }),
    http.requests.post({ url = '/v1/json-array' }),
    http.requests.patch({ url = '/v1/json-array' }),
    http.requests.put({ url = '/v1/json-array' }),
    http.requests.delete({ url = '/v1/json-array' }),
})

assert(err == nil)
assert(response ~= nil)
assert(#response == 5)

for idx = 1, 5 do
    assert(response[idx].status_code == 200)
    assert(response[idx].content_type == 'application/json')

    content = response[idx].content
    assert(content ~= nil)

    assert(table_length(content) == 4)
    assert(content[1] == 23)
    assert(content[2] == true)
    assert(content[3] == '24.23')
    assert(content[4] == 'HamalRocks')
end
