sys = require_plugin('sys')

func_req = fail_on_error(sys.funcs.create({ name = 'test-func'; inputs = {}; code = [[4 + 2]] }))
sys.await_completed(func_req)

topic_req = fail_on_error(sys.topics.create({ name = "some-amazing-topic" }))
sys.await(topic_req)

req_two = fail_on_error(sys.triggers.create_event({
    func_id = func_req.func_id,
    name = 'trigger-to-create',
    inputs = { },
    topic_id = topic_req.topic_id
}))
sys.await_completed(req_two)

assert(req_two.id ~= nil)
assert(req_two.status == 'Submitted')
assert(req_two.trigger_id ~= nil)
assert(req_two.group_id == '1')
assert(req_two.flow_id == '1')

req_two = fail_on_error(sys.triggers.get(req_two.id))

assert(req_two.type == 'Event')
assert(req_two.name == 'trigger-to-create')
assert(req_two.func.name == "test-func")
assert(req_two.flow.name == "root-flow")
assert(req_two.topic.name == "some-amazing-topic")
