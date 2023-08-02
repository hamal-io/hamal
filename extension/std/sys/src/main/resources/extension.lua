function create_extension_factory()
    local internal = _internal
    return function()
        return {

            adhoc = function(arg)
                return internal.adhoc({
                    inputs = arg.inputs or {},
                    code = arg.code or ""
                })
            end,

            exec = {
                get = function()
                    print("get exec")
                end,
                list = function()
                    print("list execs")
                end
            },
            func = {
                create = function()
                    print("create func")
                end
            }
        }
    end
end