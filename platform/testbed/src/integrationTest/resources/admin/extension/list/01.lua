sys = require('sys')
log = require('log')

local list = fail_on_error(sys.extension.list())
assert(#list == 0)

for i=1, 10  do
    local exec = fail_on_error(sys.extension.create({
        name = 'ex-' .. tostring(i),
        code_id = tostring(i),
        code_ver = i
    }))    
    sys.await_completed(exec)
end

local res = fail_on_error(sys.extension.list())

assert(#res == 10)
assert(res[5].name == 'ex-6')




