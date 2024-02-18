function table_length(tbl)
    local count = 0
    for _ in pairs(tbl) do
        count = count + 1
    end
    return count
end

function fail_on_error(err, ...)
    if err ~= nil then
        context.fail(err)
    end
    return ...
end

function find_in_list(list, key, matchValue)
    for _, value in ipairs(list) do
        if value[key] == matchValue then
            return value
        end
    end
    return nil
end
