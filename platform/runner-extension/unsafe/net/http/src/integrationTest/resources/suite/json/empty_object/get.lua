http = require('net.http')

err, response = http.get('/v1/json-empty-object')

assert(err == nil)
assert(response ~= nil)

--assert(response.ok == true)
