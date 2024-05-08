
-- As a workspace admin, I can access my own cron trigger

http = require('net.http').create({
    base_url = context.env.test_api
})

err, res = http.get({
    url = '/v1/triggers/4',
    headers = { Authorization = 'Bearer ' .. context.env.token }
})
assert(err == nil)
assert(res.status_code == 200)
assert(res.content_type == 'application/json;charset=UTF-8')

trigger_exists = res.content
assert(trigger_exists.id == '4')