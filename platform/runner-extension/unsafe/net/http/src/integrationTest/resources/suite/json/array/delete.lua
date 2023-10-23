http = require('net.http')
decimal = require('decimal')

err, response = http.delete('/v1/json-array')
assert(err == nil)
assert(response ~= nil)

assert(response.status_code == 200)
assert(response.content_type == 'application/json')

content = response.content
assert(content ~= nil)

assert(table_length(content) == 4)
assert(content[1] == 23)
assert(content[2] == true)
assert(content[3] == decimal.new('24.23'))
assert(content[4] == 'HamalRocks')
