debug = require_plugin('std.debug')
http = require('net.http').create({})

res = fail_on_error(http.post({
    url = context.env.api_host .. '/v1/anonymous-accounts',
    body =  { }
}))

debug.sleep(100)

err, res = http.get({ url = context.env.api_host .. '/v1/does/not/exists', headers = {
    ['authorization'] = 'Bearer ' .. res.content.token
} })

assert(err == nil)
assert(res.status_code == 404)
assert(res.content.message == 'Request handler not found')
