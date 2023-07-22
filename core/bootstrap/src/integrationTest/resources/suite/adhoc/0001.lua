---- Remember: Each test is an adhoc invocation
local exec_id = sys.adhoc({
    inputs = {},
    code = [[
        print("inner code execution")
    ]]
})
log.info(exec_id)

local execs = sys.list_execs()
assert(#execs == 2)

local exec = execs[1]
assert(exec.id == exec_id)

test.complete()