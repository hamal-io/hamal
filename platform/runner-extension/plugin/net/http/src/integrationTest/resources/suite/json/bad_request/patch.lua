http = require('net.http')

err, response = http.patch('/v1/json-error')
assert(err == nil)
assert(response ~= nil)

assert(response.status_code == 400)
assert(response.content_type == 'application/json')

assert(response.content ~= nil)
assert(table_length(response.content) == 3)
assert(response.content.code == 400)
assert(response.content.message == 'bad-request')
assert(response.content['boolean-value'] == true)