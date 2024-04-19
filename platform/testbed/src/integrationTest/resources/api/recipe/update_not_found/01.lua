sys = require_plugin('std.sys')
--
err, update_req = sys.recipes.update({
    id = '123456',
    name = 'update-recipe',
    inputs = {},
    value = [[i was updated]]
})
assert(err.message == 'Recipe not found')
assert(err['message'] == 'Recipe not found')
assert(recipe == nil)