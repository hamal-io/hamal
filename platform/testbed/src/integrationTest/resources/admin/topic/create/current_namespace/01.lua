sys = require("sys")

topic_req = fail_on_error(sys.topic.create({ name = "some-amazing-topic" }))
assert(topic_req.req_id ~= nil)
assert(topic_req.status == 'Submitted')
assert(topic_req.id ~= nil)
assert(topic_req.group_id == '1')
assert(topic_req.namespace_id == '1')
sys.await(topic_req)

topic_id = fail_on_error(sys.topic.get(topic_req.id))
assert(err == nil)
assert(topic_id.id == topic_req.id)
assert(topic_id.name == 'some-amazing-topic')