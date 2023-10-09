sys = require('sys')

err, exec = sys.exec.get('AB12')

assert(err ~= nil)
assert(err.message == 'Exec not found')
assert(exec == nil)