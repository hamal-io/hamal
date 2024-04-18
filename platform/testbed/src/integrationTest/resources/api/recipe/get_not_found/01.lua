sys = require_plugin('sys')
--
err, recipe = sys.recipes.get('123123123')
assert(err.message == 'Recipe not found')
assert(err['message'] == 'Recipe not found')
assert(recipe == nil)