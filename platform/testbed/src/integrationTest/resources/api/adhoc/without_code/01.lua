sys = require_plugin('std.sys')

err, res = sys.adhoc({
    inputs = {},
    code = [[ ]]
})

sys.await_completed(res)

assert(err == nil)
exec_id = res.id

