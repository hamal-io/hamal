sys = require('sys')

local create_req = fail_on_error(sys.blueprint.create({
    name = 'test-blueprint',
    inputs = {},
    value = [[40 + 2]]
}))

sys.await_completed(create_req)

local update_req = fail_on_error(sys.blueprint.update(create_req.id, {
    name = 'update-blueprint',
    inputs = {},
    value = [[i was updated]]
}))

sys.await_completed(update_req)

local blueprint = fail_on_error(sys.blueprint.get(create_req.id))

assert(blueprint.id == create_req.id)
assert(blueprint.name == 'update-blueprint')
assert(blueprint.value == [[i was updated]])