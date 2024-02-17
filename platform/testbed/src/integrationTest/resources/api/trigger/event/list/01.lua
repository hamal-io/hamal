--sys = require_plugin('sys')
--
--func_req = fail_on_error(sys.funcs.append({ name = 'test-func'; inputs = {}; code = [[4 + 2]] }))
--sys.await_completed(func_req)
--
--topic_req = fail_on_error(sys.topics.append({ name = "some-amazing-topic" }))
--sys.await(topic_req)
--
--req_one = fail_on_error(sys.triggers.create_event({
--    func_id = func_req.func_id,
--    name = 'trigger-to-append',
--    inputs = { },
--    topic_id = topic_req.topic_id
--}))
--sys.await_completed(req_one)
--
--triggers = fail_on_error(sys.triggers.list())
--assert(#triggers == 1)
--
--req_two = triggers[1]
--assert(req_two.type == 'Event')
--assert(req_two.name == 'trigger-to-append')
--assert(req_two.func.name == "test-func")
--assert(req_two.namespace.id == '1')
--assert(req_two.namespace.name == "root-namespace")
--assert(req_two.topic.name == "some-amazing-topic")