http = require 'net.http'
debug = require 'debug'

host = context.api.host

res = fail_on_error(http.post({ url = host .. '/v1/anonymous-accounts', json = { } }))

debug.sleep(100)

err, res = http.get({ url = host .. '/v1/does/not/exists', headers = {
    ['authorization'] = 'Bearer ' .. res.content.token
} })

assert(err == nil)
assert(res.status_code == 404)
assert(res.content.message == 'Request handler not found')
