sys = require('sys')
--
err, topic_id = sys.topic.get('123123123')
assert(err.message == 'Topic not found')
assert(err['message'] == 'Topic not found')
assert(topic_id == nil)