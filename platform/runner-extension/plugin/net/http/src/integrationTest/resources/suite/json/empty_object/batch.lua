local http = require('net.http')

local err, response = http.execute({
    http.requests.get('/v1/json-empty-object'),
    http.requests.post('/v1/json-empty-object'),
    http.requests.patch('/v1/json-empty-object'),
    http.requests.put('/v1/json-empty-object'),
    http.requests.delete('/v1/json-empty-object'),
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
