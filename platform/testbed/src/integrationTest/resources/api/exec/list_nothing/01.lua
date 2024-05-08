sys = require('std.sys').create({
    base_url = context.env.api_host
})

-- Remember: Each test is an adhoc invoked exec
err, execs = sys.exec.list({})
assert(err == nil)
assert(#execs == 1)

assert(execs[1].status == 'Started')