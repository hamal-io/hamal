http = require('net.http').create({
    base_url = context.env.test_url
})

decimal = require('std.decimal')

res = fail_on_error(http.patch({ url = '/v1/json-echo', json = {} }))
assert(res.content ~= nil)
assert(table_length(res.content) == 0)

res = fail_on_error(http.patch({ url = '/v1/json-echo', json = { 3, 2, { m = 1 } } }))

assert(table_length(res.content) == 3)
assert(res.content[1] == 3)
assert(res.content[2] == 2)
assert(res.content[3].m == 1)

res = fail_on_error(http.patch({ url = '/v1/json-echo', json = {
    s = 'hamal',
    d = decimal.new('12.21'),
    n = 24,
    b = true,
    m = {
        value = '0x123'
    },
} }))

assert(table_length(res.content) == 5)
assert(res.content.s == 'hamal')
assert(res.content.d == '12.21')
assert(res.content.n == 24)
assert(res.content.b == true)
assert(res.content.m.value == '0x123')

