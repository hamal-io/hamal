-- User: chen0
-- Date: 26/6/2017
-- Time: 12:48 AM
-- MIT LICENSE - https://github.com/chen0040/lua-graph/tree/master

local __list__ = {}

__list__.array_list = {}
__list__.array_list.__index = __list__.array_list

function __list__.array_list.create()
    local s = {}
    setmetatable(s, __list__.array_list)

    s.a = { nil }
    s.aLen = 1
    s.N = 0
    return s
end

function __list__.array_list.createWith(a, aLen, N)
    local s = {}
    setmetatable(s, __list__.array_list)

    s.a = a
    s.aLen = aLen
    s.N = N
    return s
end

function __list__.create()
    return __list__.array_list.create()
end

function __list__.createWith(a, aLen, N)
    return __list__.array_list.createWith(a, aLen, N)
end

function __list__.array_list:makeCopy()
    local temp = {}
    for key,val in pairs(self.a) do
        temp[key] = val
    end
    return __list__.array_list.createWith(temp, self.aLen, self.N)
end

function __list__.array_list:add(value)
    self.a[self.N] = value
    self.N = self.N + 1
    if self.N == self.aLen then
        self:resize(self.aLen * 2)
    end
end

function __list__.array_list:set(index, value)
    self.a[index] = value
end

function __list__.array_list:get(index)
    local temp = self.a[index]
    return temp
end

function __list__.array_list:removeAt(index)
    if index == self.N-1 then
        self.N = self.N - 1
        return
    end
    for i = index+1,self.N-1 do
        self.a[i-1]=self.a[i]
    end
    self.N = self.N - 1
    if self.N == math.floor(self.aLen / 4) then
        self:resize(math.floor(self.aLen / 2))
    end

end

function __list__.array_list:indexOf(value)
    if self.N == 0 then
        return -1
    end
    for i=0,self.N-1 do
        if self.a[i] == value then
            return i
        end
    end
    return -1
end

function __list__.array_list:contains(value)
    return self:indexOf(value) ~= -1
end

function __list__.array_list:remove(value)
    local index = self:indexOf(value)
    self:removeAt(index)
end

function __list__.array_list:resize(newSize)
    local temp = {}
    for i = 0,(newSize-1) do
        temp[i] = self.a[i]
    end

    self.a = temp
    self.aLen = newSize
end

function __list__.array_list:size()
    return self.N
end

function __list__.array_list:isEmpty()
    return self.N == 0
end

function __list__.array_list:enumerate()
    local temp = {}
    for i = 0,(self.N-1) do
        temp[i] = self.a[i]
    end
    return temp
end

function __list__.array_list:isSortedAscendingly(comparator)
    for i=0,(self:size()-2) do
        if comparator(a:get(i), a:get(i+1)) > 0 then
            return false
        end

    end
    return true
end

function __list__.array_list:isSortedDescendingly(comparator)
    for i=0,(self:size()-2) do
        if comparator(a:get(i), a:get(i+1)) < 0 then
            return false
        end

    end
    return true
end

