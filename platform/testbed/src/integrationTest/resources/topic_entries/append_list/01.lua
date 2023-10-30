sys = require('sys')

req = fail_on_error(sys.topic.create({ name = "topic-one" }))
sys.await(req)

topic_id = fail_on_error(sys.topic.resolve('topic-one'))

entries = fail_on_error(sys.topic.list_entries(topic_id))
assert(#entries == 0)

sys.topic.append(topic_id, { value = 'value-one' })
sys.topic.append(topic_id, { value = 'value-two' })
req = fail_on_error(sys.topic.append(topic_id, { value = 'value-three' }))
sys.await(req)
--
entries = fail_on_error(sys.topic.list_entries(topic_id))
assert(#entries == 3)