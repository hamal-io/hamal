local test = require("test")
local sys = require('sys')

local err, res = sys.adhoc({
    inputs = {},
    code = [[
        print("inner code execution")
    ]]
})

assert(err == nil)
local exec_id = res.exec_id

-- Remember: Each test is an adhoc invocation
local err, execs = sys.exec.list()
assert(err == nil)
assert(#execs == 2)

local exec = execs[1]
assert(exec.id == exec_id)

test.complete()