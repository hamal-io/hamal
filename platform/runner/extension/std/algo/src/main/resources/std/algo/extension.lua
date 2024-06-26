function extension_create()
    local export = { }
    function export.create()
        return {
            graph = {
                create = function()
                    return __graph__.create(0, directed)
                end,

                add = function(gph, from, to, weight)
                    return gph:addEdge(from, to, weight)
                end,

                vertex_count = function(gph)
                    return gph:vertexCount()
                end,

                vertices = function(gph)
                    return gph:vertices()
                end,

                remove_vertex = function(gph, from)
                    return gph:removeVertex(from)
                end,

                edges = function(gph, v)
                    return algo.list.ipairs(gph:adj(v))
                end
            },
            list = {

                create = function()
                    return __list__.create()
                end,

                add = function(lst, value)
                    return lst:add(value)
                end,

                contains = function(lst, value)
                    return lst:contains(value)
                end,

                length = function(lst)
                    return lst.N
                end,

                capacity = function(lst)
                    return lst.aLen
                end,

                ipairs = function(lst)
                    return ipairs(lst:enumerate())
                end
            }
        }
    end
    return export
end