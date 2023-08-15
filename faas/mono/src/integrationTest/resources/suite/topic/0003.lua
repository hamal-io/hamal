local sys = require('sys')
--
local error, _ = sys.topic.get('123123123')
assert(error.message == 'Topic not found')
assert(error['message'] == 'Topic not found')