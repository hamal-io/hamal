function create_extension_factory()
    local internal = _internal
    return function()
        return {
            call = function(arg1)
                -- FIXME typeof(arg1) == decimal or number
                return internal.get_block_by_id(arg1)
            end
        }
    end
end