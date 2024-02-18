local http = require_plugin('net.http')

local url = context.env.test_url .. '/v1/json-array'

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
    assert(response[idx].status_code == 200)
    assert(response[idx].content_type == 'application/json;charset=UTF-8')

    content = response[idx].content
    assert(content ~= nil)

    assert(table_length(content) == 4)
    assert(content[1] == 23)
    assert(content[2] == true)
    assert(content[3] == '24.23')
    assert(content[4] == 'HamalRocks')
end
