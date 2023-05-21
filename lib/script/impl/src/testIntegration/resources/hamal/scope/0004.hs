if false then
    local x = 10
end
assert(x == nil)

if true then
    local x = 20
end
assert(x == nil)

if false then
    g = 123
end
assert(g == nil)

if true then
    g = 321
end
assert(g == 321)