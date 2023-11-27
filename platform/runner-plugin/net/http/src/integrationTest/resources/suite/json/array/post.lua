http = require_plugin('net.http')

err, res = http.post({ url = '/v1/json-array' })
assert(err == nil)
assert(res ~= nil)

assert(res.status_code == 200)
assert(res.content_type == 'application/json')

content = res.content
assert(content ~= nil)

assert(table_length(content) == 4)
assert(content[1] == 23)
assert(content[2] == true)
assert(content[3] == '24.23')
assert(content[4] == 'HamalRocks')
