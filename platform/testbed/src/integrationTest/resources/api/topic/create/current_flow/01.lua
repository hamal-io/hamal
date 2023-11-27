sys = require_plugin('sys')

topic_req = fail_on_error(sys.topics.create({ name = "some-amazing-topic" }))
assert(topic_req.id ~= nil)
assert(topic_req.status == 'Submitted')
assert(topic_req.topic_id ~= nil)
assert(topic_req.group_id == '1')
assert(topic_req.flow_id == '1')
sys.await(topic_req)

topic_id = fail_on_error(sys.topics.get(topic_req.topic_id))
assert(topic_id.id == topic_req.topic_id)
assert(topic_id.name == 'some-amazing-topic')