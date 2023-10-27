sys = require('sys')

namespace_req = fail_on_error(sys.namespace.create({ name = 'namespace-1' }))
sys.await_completed(namespace_req)

func_req = fail_on_error(sys.func.create({ namespace_id = namespace_req.id; name = 'empty-test-func'; inputs = {}; code = [[4 + 2]] }))
sys.await_completed(func_req)

_, topic_req = sys.topic.create({ name = "some-amazing-topic" })
sys.await(topic_req)

-- trigger name is unique
trigger_req = fail_on_error(sys.trigger.create_event({
    func_id = func_req.id,
    namespace_id = '1',
    name = 'trigger-to-create',
    inputs = { },
    topic_id = topic_req.id
}))
sys.await_completed(trigger_req)

trigger_req = fail_on_error(sys.trigger.create_event({
    func_id = func_req.id,
    namespace_id = '1',
    name = 'trigger-to-create',
    inputs = { },
    topic_id = topic_req.id
}))
assert(sys.await_failed(trigger_req) == nil)

_, triggers = sys.trigger.list()
assert(#triggers == 1)

-- same name different namespace
trigger_req = fail_on_error(sys.trigger.create_event({
    func_id = func_req.id,
    namespace_id = namespace_req.id,
    name = 'trigger-to-create',
    inputs = { },
    topic_id = topic_req.id
}))
sys.await_completed(trigger_req)

_, triggers = sys.trigger.list()
assert(#triggers == 2)