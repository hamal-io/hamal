function create_extension_factory()
    local internal = _internal
    return function()
        return {

            adhoc = function(cmd)
                local err, res = internal.adhoc({
                    inputs = cmd.inputs or {},
                    code = cmd.code or ""
                })

                return err, res
            end,

            exec = {

                get = function(exec_id)
                    return internal.get_exec(exec_id)
                end,

                list = function()
                    return internal.list_execs()
                end
            },
            func = {

                create = function(cmd)
                    return internal.create_func({
                        name = cmd.name or "",
                        inputs = cmd.inputs or {},
                        code = cmd.code or ""
                    })
                end,

                get = function(func_id)
                    return internal.get_func(func_id)
                end,

                list = function()
                    return internal.list_func()
                end
            }
        }
    end
end