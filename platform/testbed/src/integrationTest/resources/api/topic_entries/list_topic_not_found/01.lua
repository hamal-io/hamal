sys = require('sys')

err, entries = sys.topic.list_entries('ABC')
assert(err ~= nil)
assert(err.message == 'Topic not found')
assert(entries == nil)