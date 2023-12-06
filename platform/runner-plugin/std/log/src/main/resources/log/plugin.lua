function plugin()
    local internal = _internal
    return function()
        local export = { }

        function export.log(level, message)
            return internal.log(level, message)
        end

        return export
    end
end