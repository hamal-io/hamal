sys = require('sys')

err, res = sys.adhoc({
    inputs = {},
    code = [[
        assert( 0 ~= 0 )
    ]]
})

sys.await_completed(res)

assert(err == nil)
exec_id = res.id

