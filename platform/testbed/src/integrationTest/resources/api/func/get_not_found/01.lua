sys = require('std.sys').create({
    base_url = context.env.api_host
})

--
err, func_one = sys.func.get('123123123')
assert(err.message == 'Func not found')
assert(func_one == nil)