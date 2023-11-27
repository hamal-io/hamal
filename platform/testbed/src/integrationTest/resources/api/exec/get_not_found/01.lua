sys = require_plugin('sys')

err, exec = sys.execs.get('AB12')

assert(err ~= nil)
assert(err.message == 'Exec not found')
assert(exec == nil)