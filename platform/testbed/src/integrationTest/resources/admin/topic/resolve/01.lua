sys = require('sys')

_, req = sys.topic.create({ name = "topic-one" })
sys.await(req)

err, topic_id = sys.topic.resolve('topic-one')
assert(err == nil)
assert(topic_id ~= nil)