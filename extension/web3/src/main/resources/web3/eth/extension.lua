function create_extension_factory()
    local internal = _internal
    return function()
        return {
            execute = function(requests)
                -- FIXME validate
                return internal.execute(requests)
            end,
        }
    end
end