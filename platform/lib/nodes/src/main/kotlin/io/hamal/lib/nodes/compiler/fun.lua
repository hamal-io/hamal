context = {}

context.n1 = {
    inputs = {
        [1] = { total_holder = 42 }
    }
}

function n1()
    return { nil, context.n1.inputs[1] }
end


function n2(args)
    if args.total_holder > 10 then
        return { nil, args }
    end

    return { nil, nil }
end

function n3(args)
    if args == nil then return nil, nil end
    print("A new token pair meets your criteria")
    return { nil }
end

R = {}

R['n1_1'] = n1()
R['n2_1'] = n2(R['n1_1'][2])
R['n3_1'] = n3(R['n2_1'][2])
