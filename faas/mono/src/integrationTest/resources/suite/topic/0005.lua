local sys = require('sys')

local _, req = sys.topic.create({ name = "topic-one" })
sys.await(req)

local error, topic_id = sys.topic.resolve('topic-one')
assert(error == nil)
assert(topic_id ~= nil)