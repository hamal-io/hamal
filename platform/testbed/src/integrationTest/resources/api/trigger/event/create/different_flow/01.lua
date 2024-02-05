sys = require_plugin('sys')

flow_req = fail_on_error(sys.flows.create({ name = "hamal::flow::rocks" }))
sys.await_completed(flow_req)

func_req = fail_on_error(sys.funcs.create({ flow_id = flow_req.flow_id, name = 'test-func'; inputs = {}; code = [[4 + 2]] }))
sys.await_completed(func_req)

topic_req = fail_on_error(sys.topics.create({ flow_id = flow_req.flow_id, name = "some-amazing-topic" }))
sys.await(topic_req)

req_one = fail_on_error(sys.triggers.create_event({
    flow_id = flow_req.flow_id,
    func_id = func_req.func_id,
    name = 'trigger-to-create',
    inputs = { },
    topic_id = topic_req.topic_id
}))
sys.await_completed(req_one)

assert(req_one.id ~= nil)
assert(req_one.status == 'Submitted')
assert(req_one.trigger_id ~= nil)
assert(req_one.group_id == '1')
assert(req_one.flow_id == flow_req.flow_id)

req_two = fail_on_error(sys.triggers.get(req_one.trigger_id))
assert(req_two.type == 'Event')
assert(req_two.name == 'trigger-to-create')
assert(req_two.func.name == "test-func")
assert(req_two.flow.name == "hamal::flow::rocks")
assert(req_two.topic.name == "some-amazing-topic")
