http = require('net.http').create({
    base_url = context.env.test_url
})

err, res = http.patch({ url = '/v1/json-error' })
assert(err == nil)
assert(res ~= nil)

assert(res.status_code == 400)
assert(res.content_type == 'application/json;charset=UTF-8')

assert(res.content ~= nil)
assert(table_length(res.content) == 3)
assert(res.content.code == 400)
assert(res.content.message == 'bad-request')
assert(res.content['boolean-value'] == true)