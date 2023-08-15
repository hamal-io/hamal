local sys = require('sys')

-- function name is unique
local err, func_req = sys.func.create({ name = 'func-name' })
sys.await_completed(func_req)
assert(err == nil)
assert(func_req ~= nil)

err, func_req = sys.func.create({ name = 'func-name' })
assert(sys.await_failed(func_req) == nil)
assert(err == nil)
assert(func_req ~= nil)

local _, funcs = sys.func.list()
assert(#funcs == 1)
