sys = require('std.sys').create({
    base_url = context.env.api_host
})

execs = fail_on_error(sys.exec.list())
assert(#execs == 3) -- 01.lua / invoked_func / 02.lua

invoked_exec = execs[2]
assert(invoked_exec.status == 'Failed')
assert(invoked_exec.correlation == '__default__')

exec = fail_on_error(sys.exec.get(invoked_exec.id))
print(dump(exec))
assert(exec ~= nil)

assert(exec.id == invoked_exec.id)
assert(exec.status == 'Failed')
assert(exec.correlation == '__default__')
