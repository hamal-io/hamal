sys = require('sys')

_, req = sys.topic.create({ name = "topic-one" })
sys.await(req)

_, topic_id = sys.topic.resolve('topic-one')

err, entries = sys.topic.list_entries(topic_id)
assert(err == nil)
assert(#entries == 0)

sys.topic.append(topic_id, { value = 'value-one' })
sys.topic.append(topic_id, { value = 'value-two' })
_, req = sys.topic.append(topic_id, { value = 'value-three' })

sys.await(req)

err, entries = sys.topic.list_entries(topic_id)
assert(err == nil)
assert(#entries == 3)