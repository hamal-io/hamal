sys = require_plugin('sys')

local create_req = fail_on_error(sys.recipes.create({
    name = 'test-recipe',
    inputs = {},
    value = [[40 + 2]]
}))

sys.await_completed(create_req)

assert(create_req.request_id ~= nil)
assert(create_req.request_status == 'Submitted')
assert(create_req.id ~= nil)

recipe = fail_on_error(sys.recipes.get(create_req.id))

assert(recipe.id == create_req.id)
assert(recipe.name == 'test-recipe')
assert(recipe.value == [[40 + 2]])