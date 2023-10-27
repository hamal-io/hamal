sys = require("sys")

err, topics = sys.topic.list()
assert(err == nil)
assert(#topics == 0)

err, topic_req = sys.topic.create({ name = "topic-one" })
assert(err == nil)
assert(topic_req.req_id ~= nil)
assert(topic_req.status == 'Submitted')
assert(topic_req.id ~= nil)

sys.await(topic_req)

err, topics = sys.topic.list()
assert(err == nil)
assert(#topics == 1)

assert(topics[1].name == 'topic-one')