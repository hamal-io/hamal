function create_extension_factory()
    local internal = _internal
    return function()
        local result = {
            request = {}
        }

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