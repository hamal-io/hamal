http = require('net.http')

err, res = http.put({ url = '/v1/json-error' })
assert(err == nil)
assert(res ~= nil)

assert(res.status_code == 400)
assert(res.content_type == 'application/json')

assert(res.content ~= nil)
assert(table_length(res.content) == 3)
assert(res.content.code == 400)
assert(res.content.message == 'bad-request')
assert(res.content['boolean-value'] == true)