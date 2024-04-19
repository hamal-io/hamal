sys = require_plugin('std.sys')

local create_req = fail_on_error(sys.recipes.create({
    name = 'test-recipe',
    inputs = {},
    value = [[40 + 2]]
}))

sys.await_completed(create_req)

local update_req = fail_on_error(sys.recipes.update({
    id = create_req.id,
    name = 'update-recipe',
    inputs = {},
    value = [[i was updated]]
}))

sys.await_completed(update_req)

local recipe = fail_on_error(sys.recipes.get(create_req.id))

assert(recipe.name == 'update-recipe')
assert(recipe.value == [[i was updated]])

update_req = fail_on_error(sys.recipes.update({
    id = create_req.id,
}))

sys.await_completed(update_req)

recipe = fail_on_error(sys.recipes.get(create_req.id))
assert(recipe.name == 'update-recipe')
assert(recipe.value == [[i was updated]])