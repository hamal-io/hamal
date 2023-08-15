local sys = require("sys")

local err, topic_one_req = sys.topic.create({ name = "some-amazing-topic" })
assert(err == nil)
assert(topic_one_req.req_id ~= nil)
assert(topic_one_req.status == 'Submitted')
assert(topic_one_req.id ~= nil)

sys.await(topic_one_req)

err, topic = sys.topic.get(topic_one_req.id)
assert(err == nil)
assert(topic.id == topic_one_req.id)
assert(topic.name == 'some-amazing-topic')