function create_capability_factory()
    return function()
        local export = { }

        function export.new(value)
            return __decimal__.new(value)
        end

        function export.to_string(dec)
            return __decimal__.tostring(dec)
        end

        function export.max(x, y)
            return __decimal__.max(x, y)
        end

        function export.min(x, y)
            return __decimal__.min(x, y)
        end

        function export.abs(x)
            return __decimal__.abs(x)
        end

        function export.sqrt(x)
            return __decimal__.sqrt(x)
        end

        function export.neg(x)
            return __decimal__.neg(x)
        end

        return export
    end
end