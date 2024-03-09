sys = require_plugin('sys')

local create_req = fail_on_error(sys.blueprints.create({
    name = 'test-blueprint',
    inputs = {},
    value = [[40 + 2]]
}))

sys.await_completed(create_req)

assert(create_req.request_id ~= nil)
assert(create_req.request_status == 'Submitted')
assert(create_req.id ~= nil)

blueprint = fail_on_error(sys.blueprints.get(create_req.id))

assert(blueprint.id == create_req.id)
assert(blueprint.name == 'test-blueprint')
assert(blueprint.value == [[40 + 2]])