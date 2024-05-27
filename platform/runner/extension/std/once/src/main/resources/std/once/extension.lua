function extension_create()
    local export = { }
    local table = require('std.table').create()

    function export.create()

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

        return function(fn)
            if not is_callable(fn) then
                require('std.throw').create().illegal_argument('Only functions and callable tables can be used once')
            end

            local result = {
                fn = fn,
                executed = false,
                result = { }
            }

            local mt = { }

            function mt.__call(self, ...)
                local params = { ... }
                if #params ~= 0 then
                    throw.illegal_argument('Once does not accept any parameter')
                end

                if not self.executed then
                    self.result = self.fn()
                    self.executed = true
                end

                return self.result
            end

            setmetatable(result, mt)
            return result

        end

    end
    return export
end