sys = require('sys')

_, req = sys.topic.create({ name = "topic-one" })
sys.await(req)

err, entries = sys.topic.list_entries(req.id)
assert(err == nil)
assert(#entries == 0)