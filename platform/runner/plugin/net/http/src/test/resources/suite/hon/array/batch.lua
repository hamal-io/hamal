local http = require_plugin('net.http')
local decimals = require('std.decimal')

local url = context.env.test_url .. '/v1/hon-array'

local err, response = http.execute({
    http.requests.get({ url = url, produces = "HON", consumes = "HON" }),
    http.requests.post({ url = url, produces = "HON", consumes = "HON" }),
    http.requests.patch({ url = url, produces = "HON", consumes = "HON" }),
    http.requests.put({ url = url, produces = "HON", consumes = "HON" }),
    http.requests.delete({ url = url, produces = "HON", consumes = "HON" })
})

assert(err == nil)
assert(response ~= nil)
assert(#response == 5)

for idx = 1, 5 do
    assert(response[idx].status_code == 200)
    assert(response[idx].content_type == 'application/hon;charset=UTF-8')

    content = response[idx].content
    assert(content ~= nil)

    assert(table_length(content) == 5)
    assert(content[1] == 23)
    assert(content[2] == true)
    assert(content[3] == '24.23')
    assert(content[4] == 'HamalRocks')
    assert(content[5] == decimals.new('13.37'))
end
