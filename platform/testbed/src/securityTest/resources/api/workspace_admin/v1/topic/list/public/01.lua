
-- As a workspace admin, I can access my own public topics

http = require('net.http').create({
    base_url = context.env.test_api
})

err, res = http.get({
    url = '/v1/topics?ids=3',
    headers = { Authorization = 'Bearer ' .. context.env.token }
})
assert(err == nil)
assert(res.status_code == 200)
assert(res.content_type == 'application/json;charset=UTF-8')

topics = res.content.topics
assert(#topics == 1)
assert(topics[1].id == '3')