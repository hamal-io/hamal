function extension_create()
    local export = { }
    function export.create()
        return {
            graph = {
                create = function()
                    return __graph__.create()
                end,
                add_edge = function(gph, v, w, weight)
                    return gph:addEdge(v, w, weight)
                end,
                vertex_count = function(gph)
                    return gph:vertexCount()
                end,
                vertices = function(gph)
                    return gph:vertices()
                end
            },
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