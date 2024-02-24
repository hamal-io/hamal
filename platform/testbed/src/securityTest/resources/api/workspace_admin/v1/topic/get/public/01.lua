
-- As a workspace admin, I can access my own workspace topic

http = require('net.http').create({
    base_url = context.env.test_api
})

err, res = http.get({
    url = '/v1/topics/3',
    headers = { Authorization = 'Bearer ' .. context.env.token }
})
assert(err == nil)
assert(res.status_code == 200)
assert(res.content_type == 'application/json;charset=UTF-8')

topic = res.content
assert(topic.id == '3')
assert(topic.type == 'Public')
