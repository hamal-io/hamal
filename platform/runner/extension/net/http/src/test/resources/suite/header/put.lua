http = require('net.http').create({
    base_url = context.env.test_url
})

decimal = require('std.decimal').create()

req_headers = {}
req_headers['Auth-ori-zation'] = 'Bearer ey.SecretToken'
req_headers['s'] = 'HamalRocks'
req_headers['d'] = decimal.new('12.21')
req_headers['n'] = 24
req_headers['b'] = true

res = fail_on_error(http.put({
    url = '/v1/headers',
    headers = req_headers
}))

headers = res.headers
assert(headers['auth-ori-zation'] == 'Bearer ey.SecretToken')

assert(headers['s'] == 'HamalRocks')
assert(headers['d'] == '12.21')
assert(headers['n'] == '24.0')
assert(headers['b'] == 'true')
