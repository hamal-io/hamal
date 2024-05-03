local http = require_plugin('net.http')
decimal = require('std.decimal')

req_headers = {}
req_headers['Auth-ori-zation'] = 'Bearer ey.SecretToken'
req_headers['s'] = 'HamalRocks'
req_headers['d'] = decimal.new('12.21')
req_headers['n'] = 24
req_headers['b'] = true

local url = context.env.test_url .. '/v1/headers'

res = fail_on_error(http.execute({
    http.requests.get({ url = url, headers = req_headers, produces = "JSON", consumes = "JSON" }),
    http.requests.post({ url = url, headers = req_headers, produces = "JSON", consumes = "JSON" }),
    http.requests.patch({ url = url, headers = req_headers, produces = "JSON", consumes = "JSON" }),
    http.requests.put({ url = url, headers = req_headers, produces = "JSON", consumes = "JSON" }),
    http.requests.delete({ url = url, headers = req_headers, produces = "JSON", consumes = "JSON" })
}))

assert(err == nil)
assert(res ~= nil)
assert(#res == 5)

for idx = 1, 5 do
    headers = res[idx].headers

    assert(headers['auth-ori-zation'] == 'Bearer ey.SecretToken')
    assert(headers['s'] == 'HamalRocks')
    assert(headers['d'] == '12.21')
    assert(headers['n'] == '24.0')
    assert(headers['b'] == 'true')
end
