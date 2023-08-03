function create_extension_factory()
    local internal = _internal
    return function()
        local result = {
            exec = { },
            func = { }
        }

        function result.adhoc(cmd)
            local err, res = internal.adhoc({
                inputs = cmd.inputs or {},
                code = cmd.code or ""
            })
            return err, res
        end

        function result.exec.get(exec_id)
            return internal.get_exec(exec_id)
        end

        function result.exec.list()
            return internal.list_execs()
        end

        function result.func.create(cmd)
            return internal.create_func({
                name = cmd.name or "",
                inputs = cmd.inputs or {},
                code = cmd.code or ""
            })
        end

        function result.func.get(func_id)
            return internal.get_func(func_id)
        end

        function result.func.list()
            return internal.list_func()
        end

        return result
    end
end