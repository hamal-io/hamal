http = require('net.http').create({
    base_url = context.env.test_url
})

err, res = http.get({ url = '/v1/json-empty-array' })
assert(err == nil)
assert(res ~= nil)

assert(res.status_code == 200)
assert(res.content_type == 'application/json;charset=UTF-8')

assert(res.content ~= nil)
assert(table_length(res.content) == 0)