-- local
local x = 10
do
    assert(x == 10)
    do
        assert(x == 10)
        local x = 20
        assert(x == 20)
    end
    assert(x == 10)
end
assert(x == 10)

--global
g = 10
do
    assert(g == 10)
    do
        assert(g == 10)
        g = 20
        assert(g == 20)
    end
    assert(g == 20)
end
assert(g == 20)