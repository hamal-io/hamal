sys = require_plugin('sys')

_, req = sys.topics.create({ name = "topic-one" })
sys.await(req)

err, topic_id = sys.topics.resolve('topic-one')
assert(err == nil)
assert(topic_id ~= nil)