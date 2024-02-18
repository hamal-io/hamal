--sys = require_plugin('sys')
--
--namespace_req = fail_on_error(sys.namespaces.append({ name = "hamal::namespace::rocks" }))
--sys.await_completed(namespace_req)
--
--func_req = fail_on_error(sys.funcs.append({ namespace_id = namespace_req.namespace_id, name = 'test-func'; inputs = {}; code = [[4 + 2]] }))
--sys.await_completed(func_req)
--
--topic_req = fail_on_error(sys.topics.append({ namespace_id = namespace_req.namespace_id, name = "some-amazing-topic" }))
--sys.await(topic_req)
--
--req_one = fail_on_error(sys.triggers.create_event({
--    namespace_id = namespace_req.namespace_id,
--    func_id = func_req.func_id,
--    name = 'trigger-to-append',
--    inputs = { },
--    topic_id = topic_req.topic_id
--}))
--sys.await_completed(req_one)
--
--assert(req_one.id ~= nil)
--assert(req_one.status == 'Submitted')
--assert(req_one.trigger_id ~= nil)
--assert(req_one.workspace_id == '1')
--assert(req_one.namespace_id == namespace_req.namespace_id)
--
--req_two = fail_on_error(sys.triggers.get(req_one.trigger_id))
--assert(req_two.type == 'Event')
--assert(req_two.name == 'trigger-to-append')
--assert(req_two.func.name == "test-func")
--assert(req_two.namespace.name == "hamal::namespace::rocks")
--assert(req_two.topic.name == "some-amazing-topic")
--
