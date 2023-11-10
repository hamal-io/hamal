sys = require('sys')

req = fail_on_error(sys.topics.create({ name = "topic-one" }))
sys.await(req)

topic_id = fail_on_error(sys.topics.resolve('topic-one'))

entries = fail_on_error(sys.topics.list_entries(topic_id))
assert(#entries == 0)

sys.topics.append(topic_id, { value = 'value-one' })
sys.topics.append(topic_id, { value = 'value-two' })
req = fail_on_error(sys.topics.append(topic_id, { value = 'value-three' }))
sys.await(req)
--
entries = fail_on_error(sys.topics.list_entries(topic_id))
assert(#entries == 3)