--sys = require_plugin('std.sys')
--
--namespace = fail_on_error(sys.namespaces.append({ name = 'namespace-1' }))
--sys.await_completed(namespace)
--
--func_one = fail_on_error(sys.funcs.append({ namespace_id = namespace.id; name = 'test-func'; inputs = {}; code = [[4 + 2]] }))
--sys.await_completed(func_one)
--
--_, topic_req = sys.topics.append({ name = "some-amazing-topic" })
--sys.await(topic_req)
--
---- trigger name is unique
--req_two = fail_on_error(sys.triggers.create_event({
--    func_id = func_one.id,
--    namespace_id = '539',
--    name = 'trigger-to-append',
--    inputs = { },
--    topic_id = topic_req.id
--}))
--sys.await_completed(req_two)
--
--req_two = fail_on_error(sys.triggers.create_event({
--    func_id = func_one.id,
--    namespace_id = '539',
--    name = 'trigger-to-append',
--    inputs = { },
--    topic_id = topic_req.id
--}))
--assert(sys.await_failed(req_two) == nil)
--
--_, triggers = sys.triggers.list()
--assert(#triggers == 1)
--
---- same name different namespace
--req_two = fail_on_error(sys.triggers.create_event({
--    func_id = func_one.id,
--    namespace_id = namespace.id,
--    name = 'trigger-to-append',
--    inputs = { },
--    topic_id = topic_req.id
--}))
--sys.await_completed(req_two)
--
--_, triggers = sys.triggers.list()
--assert(#triggers == 2)