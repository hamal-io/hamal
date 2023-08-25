local sys = require('sys')

local _, req = sys.topic.create({ name = "topic-one" })
sys.await(req)

local _, topic_id = sys.topic.resolve('topic-one')

local err, events = sys.topic.list_events(topic_id)
assert(err == nil)
assert(#events == 0)

sys.topic.append(topic_id, { value = 'value-one' })
sys.topic.append(topic_id, { value = 'value-two' })
sys.topic.append(topic_id, { value = 'value-three' })

err, events = sys.topic.list_events(topic_id)
assert(err == nil)
assert(#events == 3)