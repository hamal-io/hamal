function extension_create()
    local export = {}

    function export.create(cfg)

        local instance = { }

        function instance.new(value)
            return __decimal__.new(value)
        end

        function instance.to_string(dec)
            return __decimal__.tostring(dec)
        end

        function instance.max(x, y)
            return __decimal__.max(x, y)
        end

        function instance.min(x, y)
            return __decimal__.min(x, y)
        end

        function instance.abs(x)
            return __decimal__.abs(x)
        end

        function instance.sqrt(x)
            return __decimal__.sqrt(x)
        end

        function instance.neg(x)
            return __decimal__.neg(x)
        end

        return instance

    end

    return export
end