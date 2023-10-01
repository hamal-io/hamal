admin = require('sys')

_, req = admin.topic.create({ name = "topic-one" })
admin.await(req)

_, topic_id = admin.topic.resolve('topic-one')

err, entries = admin.topic.list_entries(topic_id)
assert(err == nil)
assert(#entries == 0)

admin.topic.append(topic_id, { value = 'value-one' })
admin.topic.append(topic_id, { value = 'value-two' })
admin.topic.append(topic_id, { value = 'value-three' })

err, entries = admin.topic.list_entries(topic_id)
assert(err == nil)
assert(#entries == 3)