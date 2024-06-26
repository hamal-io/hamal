--sys = require_plugin('std.sys')
--
--func_req = fail_on_error(sys.funcs.append({ name = 'test-func'; inputs = {}; code = [[4 + 2]] }))
--sys.await_completed(func_req)
--
--topic_req = fail_on_error(sys.topics.append({ name = "some-amazing-topic" }))
--sys.await(topic_req)
--
--req_two = fail_on_error(sys.triggers.create_event({
--    func_id = func_req.id,
--    name = 'trigger-to-append',
--    inputs = { },
--    topic_id = topic_req.topic_id
--}))
--sys.await_completed(req_two)
--
--assert(req_two.id ~= nil)
--assert(req_two.request_status == 'Submitted')
--assert(req_two.trigger_id ~= nil)
--assert(req_two.workspace_id == '539')
--assert(req_two.namespace_id == '539')
--
--req_two = fail_on_error(sys.triggers.get(req_two.id))
--
--assert(req_two.type == 'Event')
--assert(req_two.name == 'trigger-to-append')
--assert(req_two.func.name == "test-func")
--assert(req_two.namespace.name == "root-namespace")
--assert(req_two.topic.name == "some-amazing-topic")
--
