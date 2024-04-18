sys = require_plugin('sys')
recipe = sys.recipes.create({
    name = 'test-recipe',
    inputs = {},
    value = [[40 + 2]]
})