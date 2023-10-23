http = require('net.http')

err, response = http.get('/v1/json-empty-object')
assert(err == nil)
assert(response ~= nil)

assert(response.status_code == 200)
assert(response.content_type == 'application/json')

assert(response.content ~= nil)
assert(table_length(response.content) == 0)