http = require('net.http')
decimal = require('decimal')

response = fail_on_error(http.put('/v1/json-echo', { json = {} }))
assert(response.content ~= nil)
assert(table_length(response.content) == 0)

response = fail_on_error(http.put('/v1/json-echo', { json = { 3, 2, { m = 1 } } }))

assert(table_length(response.content) == 3)
assert(response.content[1] == 3)
assert(response.content[2] == 2)
assert(response.content[3].m == 1)

response = fail_on_error(http.put('/v1/json-echo', { json = {
    s = 'hamal',
    d = decimal.new('12.21'),
    n = 24,
    b = true,
    m = {
        value = '0x123'
    },
} }))

assert(table_length(response.content) == 5)
assert(response.content.s == 'hamal')
assert(response.content.d == decimal.new('12.21'))
assert(response.content.n == 24)
assert(response.content.b == true)
assert(response.content.m.value == '0x123')

