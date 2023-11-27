sys = require_plugin('sys')
err, res = sys.adhoc({ inputs = {}, code = [[assert(0 ~= 1)]] })
sys.await_completed(res)
assert(err == nil)