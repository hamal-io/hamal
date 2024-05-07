sys = require_plugin('std.sys')

err, entries = sys.topics.list_entries('ABC')
assert(err ~= nil)
assert(err.message == 'Topic not found')
assert(entries == nil)