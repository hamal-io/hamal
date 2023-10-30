sys = require('sys')

namespace = fail_on_error(sys.namespace.create({ name = 'namespace-1' }))
sys.await_completed(namespace)

func = fail_on_error(sys.func.create({ namespace_id = namespace.id; name = 'test-func'; inputs = {}; code = [[4 + 2]] }))
sys.await_completed(func)

_, topic_req = sys.topic.create({ name = "some-amazing-topic" })
sys.await(topic_req)

-- trigger name is unique
trigger = fail_on_error(sys.trigger.create_event({
    func_id = func.id,
    namespace_id = '1',
    name = 'trigger-to-create',
    inputs = { },
    topic_id = topic_req.id
}))
sys.await_completed(trigger)

trigger = fail_on_error(sys.trigger.create_event({
    func_id = func.id,
    namespace_id = '1',
    name = 'trigger-to-create',
    inputs = { },
    topic_id = topic_req.id
}))
assert(sys.await_failed(trigger) == nil)

_, triggers = sys.trigger.list()
assert(#triggers == 1)

-- same name different namespace
trigger = fail_on_error(sys.trigger.create_event({
    func_id = func.id,
    namespace_id = namespace.id,
    name = 'trigger-to-create',
    inputs = { },
    topic_id = topic_req.id
}))
sys.await_completed(trigger)

_, triggers = sys.trigger.list()
assert(#triggers == 2)