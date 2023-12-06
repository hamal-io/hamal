http = require('net.http').create({
    base_url = context.env.test_url
})

err, res = http.post({ url = '/v1/json-empty-object' })
assert(err == nil)
assert(res ~= nil)

assert(res.status_code == 200)
assert(res.content_type == 'application/json')

assert(res.content ~= nil)
assert(table_length(res.content) == 0)