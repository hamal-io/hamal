sys = require("sys")

topics = fail_on_error(sys.topic.list())
assert(#topics == 0)

topic_req = fail_on_error(sys.topic.create({ name = "topic-one" }))
sys.await(topic_req)

topics = fail_on_error(sys.topic.list())
assert(#topics == 1)
assert(topics[1].name == 'topic-one')