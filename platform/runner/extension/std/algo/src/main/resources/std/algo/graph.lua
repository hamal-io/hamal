-- User: chen0
-- Date: 26/6/2017
-- Time: 12:48 AM
-- MIT LICENSE - https://github.com/chen0040/lua-graph/tree/master

local __graph__ = {}

__graph__.__index = __graph__

__graph__.edge = {}
__graph__.edge.__index = __graph__.edge

function __graph__.edge.create(from, to, weight)
    local s = {}
    setmetatable(s, __graph__.edge)

    if weight == nil then
        weight = 1.0
    end

    s.from = from
    s.to = to
    s.weight = weight

    return s
end

function __graph__.edge:other(x)
    if x == self.from then
        return self.to
    else
        return self.from
    end

end

function __graph__.create(from, directed)
    local g = {}
    setmetatable(g, __graph__)

    from = from or 0

    if directed == nil then
        directed = false
    end

    g.vertexList = __list__.create()
    g.adjList = {}

    if from > 0 then
        for from = 0, from - 1 do
            g.vertexList:add(from)
            g.adjList[from] = __list__.create()
        end
    end
    g.directed = directed

    return g
end

function __graph__:vertexCount()
    return self.vertexList:size()
end

function __graph__:vertices()
    return self.vertexList
end

function __graph__.createFromVertexList(vertices, directed)
    local g = {}
    setmetatable(g, __graph__)

    if directed == nil then
        directed = false
    end

    g.vertexList = vertices
    g.adjList = {}
    for i = 0, g.vertexList:size() - 1 do
        local from = g.vertexList:get(i)
        g.adjList[from] = __list__.create()
    end
    g.directed = directed

    return g
end

function __graph__:addVertexIfNotExists(from)
    if self.vertexList:contains(from) then
        return false
    else
        self.vertexList:add(from)
        self.adjList[from] = __list__.create()
        return true
    end
end

function __graph__:removeVertex(from)
    if self.vertexList:contains(from) then
        self.vertexList:remove(from)
        self.adjList[from] = nil
        for i = 0, self.vertexList:size() - 1 do
            local to = self.vertexList:get(i)
            local adj_w = self.adjList[to]
            for k = 0, adj_w:size() - 1 do
                local e = adj_w:get(k)
                if e:other(to) == from then
                    adj_w:removeAt(k)
                    break
                end

            end

        end

    end
end

function __graph__:containsVertex(from)
    return self.vertexList:contains(from)
end

function __graph__:adj(from)
    return self.adjList[from]
end

function __graph__:addEdge(from, to, weight)
    local e = __graph__.edge.create(from, to, weight)
    self:addVertexIfNotExists(from)
    self:addVertexIfNotExists(to)
    if self.directed then
        self.adjList[e.from]:add(e)
    else
        self.adjList[e.from]:add(e)
        self.adjList[e.to]:add(e)
    end

end

function __graph__:reverse()
    local g = __graph__.createFromVertexList(self.vertexList, self.directed)
    for k = 0, self:vertexCount() - 1 do
        local from = self:vertexAt(k)
        local adj_v = self:adj(from)
        for i = 0, adj_v:size() - 1 do
            local e = adj_v:get(i)
            g:addEdge(e.to, e.from, e.weight)
        end

    end

    return g
end

function __graph__:vertexAt(i)
    return self.vertexList:get(i)
end

function __graph__:edges()
    local list = __list__.create()

    for i = 0, self.vertexList:size() - 1 do
        local from = self.vertexList:get(i)
        local adj_v = self:adj(from)
        for j = 0, adj_v:size() - 1 do
            local e = adj_v:get(j)
            local to = e:other(from)
            if self.directed == true or to > from then
                list:add(e)
            end

        end

    end

    return list
end

function __graph__:hasEdge(from, to)
    local adj_v = self:adj(from)
    for i = 0, adj_v:size() - 1 do
        local e = adj_v:get(i)
        if e.to == to then
            return true
        end
    end
    return false
end