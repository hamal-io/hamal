sys = require_plugin('sys')
--
err, recipe = sys.recipes.get('123123123')
assert(err.message == 'recipe not found')
assert(err['message'] == 'recipe not found')
assert(recipe == nil)