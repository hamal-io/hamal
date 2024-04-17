sys = require_plugin('sys')
--
err, update_req = sys.recipes.update({
    id = '123456',
    name = 'update-recipe',
    inputs = {},
    value = [[i was updated]]
})
assert(err.message == 'recipe not found')
assert(err['message'] == 'recipe not found')
assert(recipe == nil)