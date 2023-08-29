function create_extension_factory()
    local internal = _internal
    return function()
        return {
            call = function()
                internal.test_extension_call()
            end,
            some_number = 42,
            some_boolean = true
        }
    end
end