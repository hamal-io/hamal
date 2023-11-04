http = require 'net.http'

host = context.api.host

res = fail_on_error(http.post(host .. '/v1/anonymous-accounts', { json = { } }))

err, res = http.get(host .. '/v1/does/not/exists', { headers = {
    ['authorization'] = 'Bearer ' .. res.content.token
} })

assert(err == nil)
assert(res.status_code == 404)
assert(res.content.message == 'Request handler not found')
