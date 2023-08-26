function create_extension_factory()

    local decimal_meta_table = {
        __div = function(self, other) return decimal_new(__decimal__.div(self.data, other)) end,
        __tostring = function(self) return __decimal__.tostring(self.data) end,
    }

    function decimal_new(value)
        local result =  {
            __typename = __decimal__.__typename,
            __type_id = __decimal__.__type_id,
            data = __decimal__.new(value),
        }
        setmetatable(result, decimal_meta_table)
        function result.to_string() return __decimal__.tostring(result.data) end
        return result
    end



    return function()
        local export = { }

        function export.new(value) return decimal_new(value) end
        function export.to_string(dec) return __decimal__.tostring(dec.data) end

        return export
    end
end