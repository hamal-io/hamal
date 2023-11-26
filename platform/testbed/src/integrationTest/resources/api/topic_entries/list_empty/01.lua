sys = require_plugin('sys')

_, req = sys.topics.create({ name = "topic-one" })
sys.await(req)

err, entries = sys.topics.list_entries(req.id)
assert(err == nil)
assert(#entries == 0)