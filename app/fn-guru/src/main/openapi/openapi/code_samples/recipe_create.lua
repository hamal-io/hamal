<<<<<<< HEAD:app/fn-guru/src/main/openapi/openapi/code_samples/recipe_create.lua
sys = require_plugin('sys')
recipe = sys.recipes.create({
    name = 'test-recipe',
=======
sys = require_plugin('std.sys')
blueprint = sys.blueprints.create({
    name = 'test-blueprint',
>>>>>>> 900aead8d (Moves std extension / plugins into std directory):app/fn-guru/src/main/openapi/openapi/code_samples/blueprint_create.lua
    inputs = {},
    value = [[40 + 2]]
})