function extension_create()

    local memoize = {
        _VERSION = 'memoize v2.0',
        _DESCRIPTION = 'Memoized functions in Lua',
        _URL = 'https://github.com/kikito/memoize.lua',
        _LICENSE = [[
    MIT LICENSE

    Copyright (c) 2018 Enrique García Cota

    Permission is hereby granted, free of charge, to any person obtaining a
    copy of this software and associated documentation files (the
    "Software"), to deal in the Software without restriction, including
    without limitation the rights to use, copy, modify, merge, publish,
    distribute, sublicense, and/or sell copies of the Software, and to
    permit persons to whom the Software is furnished to do so, subject to
    the following conditions:

    The above copyright notice and this permission notice shall be included
    in all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
    OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
    MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
    IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
    CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
    TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
    SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
  ]]
    }
    -- Inspired by http://stackoverflow.com/questions/129877/how-do-i-write-a-generic-memoize-function

    local table = require('std.table').create()
    local unpack = unpack or table.unpack

    local function is_callable(f)
        local tf = type(f)
        if tf == 'function' then
            return true
        end
        if tf == 'table' then
            local mt = getmetatable(f)
            return type(mt) == 'table' and is_callable(mt.__call)
        end
        return false
    end

    local function cache_get(cache, params)
        local node = cache
        for i = 1, #params do
            node = node.children and node.children[params[i]]
            if not node then
                return nil
            end
        end
        return node.results
    end

    local function cache_put(cache, params, results)
        local node = cache
        local param
        for i = 1, #params do
            param = params[i]
            node.children = node.children or {}
            node.children[param] = node.children[param] or {}
            node = node.children[param]
        end
        node.results = results
    end

    local export = {}

    function export.create()

        return function(fn)
            cache = cache or {}
            
            if not is_callable(fn) then
                require('std.throw').create().illegal_argument('Only functions and callable tables are memoizable')
            end

            return function(...)
                local params = { ... }

                local results = cache_get(cache, params)
                if not results then
                    results = { fn(...) }
                    cache_put(cache, params, results)
                end

                return unpack(results)
            end
        end
    end

    return export
end