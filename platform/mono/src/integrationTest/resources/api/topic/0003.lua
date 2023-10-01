admin = require('sys')
--
err, _ = admin.topic.get('123123123')
assert(err.message == 'Topic not found')
assert(err['message'] == 'Topic not found')