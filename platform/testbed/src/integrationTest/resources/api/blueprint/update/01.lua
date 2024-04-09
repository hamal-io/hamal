sys = require_plugin('sys')

local create_req = fail_on_error(sys.blueprints.create({
    name = 'test-blueprint',
    inputs = {},
    value = [[40 + 2]]
}))

sys.await_completed(create_req)

local update_req = fail_on_error(sys.blueprints.update({
    id = create_req.id,
    name = 'update-blueprint',
    inputs = {},
    value = [[i was updated]]
}))

sys.await_completed(update_req)

local blueprint = fail_on_error(sys.blueprints.get(create_req.id))

assert(blueprint.name == 'update-blueprint')
assert(blueprint.value == [[i was updated]])

update_req = fail_on_error(sys.blueprints.update({
    id = create_req.id,
}))

sys.await_completed(update_req)

blueprint = fail_on_error(sys.blueprints.get(create_req.id))
assert(blueprint.name == 'update-blueprint')
assert(blueprint.value == [[i was updated]])