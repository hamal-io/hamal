function create_extension_factory()
    local internal = _internal
    return function()
        local export = {
            abi = {
                types = {
                    UINT_8 = 'uint256',
                    UINT_256 = 'uint256'
                }
            },
            request = {}
        }

        function export.abi.decode_parameter(type, value)
            return internal.decode_parameter(type, value)
        end

        function export.request.get_block(block)
            -- FIXME make sure its a number
            return { type = "get_block", block = block }
        end

        function export.execute(requests)
            -- FIXME validate
            return internal.execute(requests)
        end

        function export.get_block(block)
            return export.execute({ export.request.get_block(block) })
        end

        return export
    end
end