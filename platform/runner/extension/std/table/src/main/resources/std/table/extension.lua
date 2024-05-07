function extension_create()
    local export = {}

    function export.create()

        local instance = { }

        function instance.length(tbl)
            local count = 0
            for _ in pairs(tbl) do
                count = count + 1
            end
            return count
        end

        function instance.insert(tbl, ...)
            return __table__.__insert(tbl, instance.unpack(...))
        end

        function instance.unpack(...)
            local args = { ... }
            return __table__.__unpack(args)
        end

        function instance.find(tbl, key, value)
            for _, v in ipairs(tbl) do
                if v[key] == value then
                    return v
                end
            end
            return nil
        end

        function instance.contains (tbl, value)
            for _, v in ipairs(tbl) do
                if v == value then
                    return true
                end
            end
            return false
        end

        return instance
    end

    return export
end