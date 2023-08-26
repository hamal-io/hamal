function create_extension_factory()
    return function()
        local export = { }

        function export.new(value)
            return __decimal__.new(value)
        end

        function export.to_string(dec)
            return __decimal__.tostring(dec)
        end

        return export
    end
end