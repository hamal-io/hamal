function some_fn()
    local err = nil
    local result_one = 1
    local result_two = 2
    return err, result_one, result_two
end

local err, one, two = some_fn()
if err ~= nil then
    ctx.fail('terminate execution and fail exec')
end
print(one)
print(two)

-- same behaviour but less code
local one, two = fail_on_error(some_fn())
print(one)
print(two)