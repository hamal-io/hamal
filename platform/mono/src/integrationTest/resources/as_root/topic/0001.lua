sys = require("sys")

err, topics = sys.topic.list()
assert(err == nil)
assert(#topics == 0)

err, topic_one_req = sys.topic.create({ name = "topic-one" })
assert(err == nil)
assert(topic_one_req.req_id ~= nil)
assert(topic_one_req.status == 'Submitted')
assert(topic_one_req.id ~= nil)

sys.await(topic_one_req)

err, topics = sys.topic.list()
assert(err == nil)
assert(#topics == 1)

assert(topics[1].name == 'topic-one')