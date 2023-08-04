function create_extension_factory()
    local internal = _internal
    return function()
        local result = {
            abi = {
                types = {
                    UINT_8 = 'uint256',
                    UINT_256 = 'uint256'
                }
            },
            request = {}
        }

        function result.abi.decode_parameter(type, value)
            return internal.decode_parameter(type, value)
        end

        function result.request.get_block(block)
            -- FIXME make sure its a number
            return { type = "get_block", block = block }
        end

        function result.execute(requests)
            -- FIXME validate
            return internal.execute(requests)
        end

        function result.get_block(block)
            return result.execute({ result.request.get_block(block) })
        end

        return result
    end
end