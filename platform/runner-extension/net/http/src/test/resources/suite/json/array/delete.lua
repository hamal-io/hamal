http = require('net.http').create({
    base_url = context.env.test_url
})

err, res = http.delete({ url = '/v1/json-array' })
assert(err == nil)
assert(res ~= nil)

assert(res.status_code == 200)
assert(res.content_type == 'application/json;charset=UTF-8')

content = res.content
assert(content ~= nil)

assert(table_length(content) == 4)
assert(content[1] == 23)
assert(content[2] == true)
assert(content[3] == '24.23')
assert(content[4] == 'HamalRocks')
