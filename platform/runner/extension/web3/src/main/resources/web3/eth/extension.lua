local hamal_provider_create = function(opts)
    url = opts.url or 'https://web3-proxy.hamal.io'

    return {
        name = 'hamal',
        url = url,
        get_block_by_number = function(block_number)
            print('fetching ' .. block_number .. ' from url' .. url)
        end
    }
end

local function abi_decode_parameter(type, value)
    print("Do some decoding of " .. type)
end

function extension_create()
    return {
        create = function(options)
            opts = options or {}
            opts.provider = hamal_provider_create(opts)

            local provider = opts.provider
            return {
                chain_id = 1,
                provider = provider,
                abi = {
                    type = {
                        uint8 = {
                            name = 'uint8'
                        },
                        uint256 = {
                            name = 'uint256'
                        }
                    },
                    decode_parameter = abi_decode_parameter
                },
                get_block_by_number = provider.get_block_by_number
            }
        end
    }
end