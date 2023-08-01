function create_extension_factory()
    local internal = _internal
    return function()
        return {
            abi = {
                decode = function(type, value)
                    print("decoding", type, value)
                end
            },
            call = function(arg1)
                return internal.get_block_by_id(arg1)
            end
        }
    end
end