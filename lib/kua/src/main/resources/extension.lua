function create_extension_factory()
    local internal = extension.import("eth")
    return function()
        return {
            call = function(arg1)
                print(internal.get_block)
            end
        }
    end
end