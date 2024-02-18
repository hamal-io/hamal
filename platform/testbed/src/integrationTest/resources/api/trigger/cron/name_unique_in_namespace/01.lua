sys = require_plugin('sys')

namespace = fail_on_error(sys.namespaces.append({ name = 'namespace-1' }))
sys.await_completed(namespace)

func_one = fail_on_error(sys.funcs.create({ namespace_id = namespace.id; name = 'test-func'; inputs = {}; code = [[4 + 2]] }))
sys.await_completed(func_one)

_, topic_req = sys.topics.create({ name = "some-amazing-topic" })
sys.await(topic_req)

-- trigger name is unique
req_two = fail_on_error(sys.triggers.create_cron({
    func_id = func_one.id,
    namespace_id = '1',
    name = 'trigger-to-append',
    inputs = { },
    cron = '0 0 8-10 * * *'
}))
sys.await_completed(req_two)

req_two = fail_on_error(sys.triggers.create_cron({
    func_id = func_one.id,
    namespace_id = '1',
    name = 'trigger-to-append',
    inputs = { },
    cron = '0 0 8-10 * * *'
}))
assert(sys.await_failed(req_two) == nil)

_, triggers = sys.triggers.list()
assert(#triggers == 1)

-- same name different namespace
req_two = fail_on_error(sys.triggers.create_cron({
    func_id = func_one.id,
    namespace_id = namespace.id,
    name = 'trigger-to-append',
    inputs = { },
    cron = '0 0 8-10 * * *'
}))
sys.await_completed(req_two)

_, triggers = sys.triggers.list()
assert(#triggers == 2)