sys = require('sys')
--
err, _ = sys.topic.get('123123123')
assert(err.message == 'Topic not found')
assert(err['message'] == 'Topic not found')