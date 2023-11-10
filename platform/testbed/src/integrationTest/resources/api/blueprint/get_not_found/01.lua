sys = require('sys')
--
err, blueprint = sys.blueprints.get('123123123')
assert(err.message == 'Blueprint not found')
assert(err['message'] == 'Blueprint not found')
assert(blueprint == nil)