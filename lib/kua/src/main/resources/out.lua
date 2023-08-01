print("out")

local eth = test.require('web3/eth')
print(eth.call(23))

print("done")

--local x = ethFactory()
--x.call(1)
--x.call("some str")
--
--
--for k,v in pairs(_G) do
--    print(k, v)
--end