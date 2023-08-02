function create_extension_factory()
    local internal = _internal
    return function()
        return {

            adhoc = function(arg)
                local err, res = internal.adhoc({
                    inputs = arg.inputs or {},
                    code = arg.code or ""
                })

                return err, res
            end,

            exec = {
                get = function()
                    print("get exec")
                end,
                list = function()
                    return internal.list_execs()
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