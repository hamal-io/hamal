local x = 10
do
    local y = 23
    assert(x == 10)
    assert(y == 23)
    do
        local z = 46
        assert(x == 10)
        assert(y == 23)
        assert(z == 46)
        do
            assert(x == 10)
            assert(y == 23)
            assert(z == 46)
        end
    end
end

assert(x == 10)
assert(nil == y)