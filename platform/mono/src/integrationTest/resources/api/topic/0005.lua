admin = require('sys')

_, req = admin.topic.create({ name = "topic-one" })
admin.await(req)

err, topic_id = admin.topic.resolve('topic-one')
assert(err == nil)
assert(topic_id ~= nil)