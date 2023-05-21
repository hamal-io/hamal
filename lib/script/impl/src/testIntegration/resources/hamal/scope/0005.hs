-- executes if block in new scope
if true then
    local x = 1
elseif false then
    local x = 2
else
    local x = 3
end
assert(x == nil)

-- executes elseif block in new scope
if false then
    local x = 1
elseif true then
    local x = 2
else
    local x = 3
end
assert(x == nil)

-- executes else block in new scope
if false then
    local x = 1
elseif false then
    local x = 2
else
    local x = 3
end
assert(x == nil)
