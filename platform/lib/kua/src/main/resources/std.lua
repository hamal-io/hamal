function table_length(tbl)
    local count = 0
    for _ in pairs(tbl) do
        count = count + 1
    end
    return count
end

function fail_on_error(err, ...)
    if err ~= nil then
        ctx.fail(err)
    end
    return ...
end