sys = require_plugin('std.sys')

err, execs = sys.execs.list()
assert(err == nil)
assert(#execs == 3) -- 01.lua / invoked_func / 01.lua

invoked_exec = execs[2]
assert(invoked_exec.status == 'Failed')
assert(invoked_exec.correlation_id == '__default__')

err, exec = sys.execs.get(invoked_exec.id)
assert(err == nil)
assert(exec ~= nil)

assert(exec.id == invoked_exec.id)
assert(exec.status == 'Failed')
assert(exec.correlation.id == '__default__')
