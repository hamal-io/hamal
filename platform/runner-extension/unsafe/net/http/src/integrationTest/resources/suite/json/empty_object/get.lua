http = require('net.http')

err, response = http.get('/v1/json-empty-object')
assert(err == nil)
assert(response ~= nil)

print(response.data)

--assert(response.ok == true)
