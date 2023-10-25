local http = require('net.http')

local err, response = http.execute({
    http.requests.get('/v1/json-empty-array'),
    http.requests.post('/v1/json-empty-array'),
    http.requests.patch('/v1/json-empty-array'),
    http.requests.put('/v1/json-empty-array'),
    http.requests.delete('/v1/json-empty-array'),
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
