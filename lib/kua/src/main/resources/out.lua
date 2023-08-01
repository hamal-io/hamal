print("out")

local eth = require('web3/eth')

print(eth.call(23))
print(eth.call(23))
print(eth.call(23))


for k,v in pairs(_G) do
    print(k, v)
end

print("done")

