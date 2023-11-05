local http = require('net.http')
local decimal = require('decimal')

local err, response = http.execute({
    http.requests.get('/v1/json-array'),
    http.requests.post('/v1/json-array'),
    http.requests.patch('/v1/json-array'),
    http.requests.put('/v1/json-array'),
    http.requests.delete('/v1/json-array'),
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
