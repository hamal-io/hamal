function extension_create()
    local export = { }
    function export.create()
        print(__list__.create())

        return {
            list = {
                create = function()
                    return __list__.create()
                end,

                add = function(lst, value)
                    return lst:add(value)
                end,

                length = function(lst)
                    return lst.N
                end,

                capacity = function(lst)
                    return lst.aLen
                end
            }
        }
    end
    return export
end