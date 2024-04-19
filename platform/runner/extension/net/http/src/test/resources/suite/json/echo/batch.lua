http = require('net.http').create({
    base_url = context.env.test_url
})

decimal = require('std.decimal')

local json = {
    s = 'hamal',
    d = decimal.new('12.21'),
    n = 24,
    b = true,
    m = {
        value = '0x123'
    },
}

local err, response = http.execute({
    http.requests.post({ url = '/v1/json-echo', json = json }),
    http.requests.patch({ url = '/v1/json-echo', json = json }),
    http.requests.put({ url = '/v1/json-echo', json = json }),
})

assert(err == nil)
assert(response ~= nil)
assert(#response == 3)

for idx = 1, 3 do
    assert(response[idx].status_code == 200)
    assert(response[idx].content_type == 'application/json;charset=UTF-8')

    local content = response[idx].content

    assert(table_length(content) == 5)
    assert(content.s == 'hamal')
    assert(content.d == '12.21')
    assert(content.n == 24)
    assert(content.b == true)
    assert(content.m.value == '0x123')

end
