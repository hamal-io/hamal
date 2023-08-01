print("out")

local eth = require('web3/eth')

eth.config.update({host = "http://proxy"})
print(eth.config.get())

for k,v in pairs(eth.config.get()) do
    print(k,v)
end


--print(eth.call(23).id)
--print(eth.call(23)['id'])
--print(eth.call(23))
--print(eth.call(23))
--print(eth.call(23))
--print(eth.call(23))
--
--eth.abi.decode('unit256', '0x000000...01')
--
--for k, v in pairs(_G) do
--    print(k, v)
--end
--
--print("done")
