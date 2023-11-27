http = require_plugin('net.http')

err, res = http.put({ url = '/v1/json-empty-array' })
assert(err == nil)
assert(res ~= nil)

assert(res.status_code == 200)
assert(res.content_type == 'application/json')

assert(res.content ~= nil)
assert(table_length(res.content) == 0)