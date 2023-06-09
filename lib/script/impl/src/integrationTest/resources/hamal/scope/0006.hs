-- executes block in new scope
for i = 1, 10 do
    local x = 32
end

assert(x == nil)

-- executes block in new scope, but sets global ident
for i = 1, 10 do
    g = i
end

assert(g == 10)