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

function dump(o)
    if type(o) == 'table' then
        local s = '{ '
        for k, v in pairs(o) do
            if type(k) ~= 'number' then
                k = '"' .. k .. '"'
            end
            s = s .. '[' .. k .. '] = ' .. dump(v) .. ','
        end
        return s .. '} '
    else
        return tostring(o)
    end
end

function handle_error(err, resp)
    if err ~= nil then
        return err, nil
    end

    if resp.content['class'] == 'ApiError' then
        print('ApiError: ' .. resp.content['message'])
        err = {
            type = 'ApiError',
            message = resp.content['message']
        }
        return err, nil
    end

    return nil, resp
end