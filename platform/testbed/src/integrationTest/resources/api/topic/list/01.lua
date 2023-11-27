sys = require_plugin('sys')

topics = fail_on_error(sys.topics.list())
assert(#topics == 0)

topic_req = fail_on_error(sys.topics.create({ name = "topic-one" }))
sys.await(topic_req)

topics = fail_on_error(sys.topics.list())
assert(#topics == 1)
assert(topics[1].name == 'topic-one')