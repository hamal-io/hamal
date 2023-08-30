local sys = require('sys')

local _, req = sys.topic.create({ name = "topic-one" })
sys.await(req)

local _, topic_id = sys.topic.resolve('topic-one')

local err, entries = sys.topic.list_entries(topic_id)
assert(err == nil)
assert(#entries == 0)

sys.topic.append(topic_id, { value = 'value-one' })
sys.topic.append(topic_id, { value = 'value-two' })
sys.topic.append(topic_id, { value = 'value-three' })

err, entries = sys.topic.list_entries(topic_id)
assert(err == nil)
assert(#entries == 3)