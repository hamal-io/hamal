function plugin_factory_create()
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

        function export.request.call(cmd)
            cmd.type = "call"
            return cmd
        end

        function export.execute(requests)
            -- FIXME validate
            return internal.execute(requests)
        end

        function export.get_block(block)
            err, responses = export.execute({ export.request.get_block(block) })
            if err ~= nil then
                return err, nil
            end
            return nil, responses[1]
        end

        return export
    end
end