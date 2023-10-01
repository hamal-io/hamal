admin = require("sys")

err, topic_one_req = admin.topic.create({ name = "some-amazing-topic" })
assert(err == nil)
assert(topic_one_req.req_id ~= nil)
assert(topic_one_req.status == 'Submitted')
assert(topic_one_req.id ~= nil)
admin.await(topic_one_req)

err, topic = admin.topic.get(topic_one_req.id)
assert(err == nil)
assert(topic.id == topic_one_req.id)
assert(topic.name == 'some-amazing-topic')