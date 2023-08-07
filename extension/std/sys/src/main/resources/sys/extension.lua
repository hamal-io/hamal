function create_extension_factory()
    local internal = _internal
    return function()
        local export = {
            exec = { },
            func = { }
        }

        function export.adhoc(cmd)
            local err, res = internal.adhoc({
                inputs = cmd.inputs or {},
                code = cmd.code or ""
            })
            return err, res
        end

        function export.exec.get(exec_id)
            return internal.get_exec(exec_id)
        end

        function export.exec.list()
            return internal.list_execs()
        end

        function export.func.create(cmd)
            return internal.create_func({
                name = cmd.name or "",
                inputs = cmd.inputs or {},
                code = cmd.code or ""
            })
        end

        function export.func.get(func_id)
            return internal.get_func(func_id)
        end

        function export.func.list()
            return internal.list_func()
        end

        return export
    end
end