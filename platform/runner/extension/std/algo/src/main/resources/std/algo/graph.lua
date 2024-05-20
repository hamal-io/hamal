-- User: chen0
-- Date: 26/6/2017
-- Time: 12:48 AM
-- MIT LICENSE - https://github.com/chen0040/lua-graph/tree/master

local __graph__ = {}

__graph__.__index = __graph__

__graph__.edge = {}
__graph__.edge.__index = __graph__.edge

function __graph__.edge.create(v, w, weight)
    local s = {}
    setmetatable(s, __graph__.edge)

    if weight == nil then
        weight = 1.0
    end

    s.v = v
    s.w = w
    s.weight = weight

    return s
end

function __graph__.edge:from()
    return self.v
end

function __graph__.edge:to()
    return self.w;
end

function __graph__.edge:either()
    return self.v
end

function __graph__.edge:other(x)
    if x == self.v then
        return self.w
    else
        return self.v
    end

end

function __graph__.create(V, directed)
    local g = {}
    setmetatable(g, __graph__)

    V = V or 0

    if directed == nil then
        directed = false
    end

    g.vertexList = __list__.create()
    g.adjList = {}

    if V > 0 then
        for v = 0, V - 1 do
            g.vertexList:add(v)
            g.adjList[v] = __list__.create()
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
        local v = g.vertexList:get(i)
        g.adjList[v] = __list__.create()
    end
    g.directed = directed

    return g
end

function __graph__:addVertexIfNotExists(v)
    if self.vertexList:contains(v) then
        return false
    else
        self.vertexList:add(v)
        self.adjList[v] = __list__.create()
        return true
    end
end

function __graph__:removeVertex(v)
    if self.vertexList:contains(v) then
        self.vertexList:remove(v)
        self.adjList[v] = nil
        for i = 0, self.vertexList:size() - 1 do
            local w = self.vertexList:get(i)
            local adj_w = self.adjList[w]
            for k = 0, adj_w:size() - 1 do
                local e = adj_w:get(k)
                if e:other(w) == v then
                    adj_w:removeAt(k)
                    break
                end

            end

        end

    end
end

function __graph__:containsVertex(v)
    return self.vertexList:contains(v)
end

function __graph__:adj(v)
    return self.adjList[v]
end

function __graph__:addEdge(v, w, weight)
    local e = __graph__.edge.create(v, w, weight)
    self:addVertexIfNotExists(v)
    self:addVertexIfNotExists(w)
    if self.directed then
        self.adjList[e:from()]:add(e)
    else
        self.adjList[e:from()]:add(e)
        self.adjList[e:to()]:add(e)
    end

end

function __graph__:reverse()
    local g = __graph__.createFromVertexList(self.vertexList, self.directed)
    for k = 0, self:vertexCount() - 1 do
        local v = self:vertexAt(k)
        local adj_v = self:adj(v)
        for i = 0, adj_v:size() - 1 do
            local e = adj_v:get(i)
            g:addEdge(e:to(), e:from(), e.weight)
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
        local v = self.vertexList:get(i)
        local adj_v = self:adj(v)
        for j = 0, adj_v:size() - 1 do
            local e = adj_v:get(j)
            local w = e:other(v)
            if self.directed == true or w > v then
                list:add(e)
            end

        end

    end

    return list
end

function __graph__:hasEdge(v, w)
    local adj_v = self:adj(v)
    for i = 0, adj_v:size() - 1 do
        local e = adj_v:get(i)
        if e:to() == w then
            return true
        end
    end
    return false
end