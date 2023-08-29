-- test mpdecimal library

local D=require"mpdecimal"
D.digits(50)
D.pi=D.new"3.1415926535897932384626433832795028841971693993751058209749445923078164062862090"

------------------------------------------------------------------------------
print(D.version)

------------------------------------------------------------------------------
print""
print"Exp by series"
function test(w,x,t)
	local s=t
	local S
	for n=1,1000 do
		t=t*x/n
		S=s
		s=s+t
		if s==S then print(w,n,s) return end
	end
	print(w,'*',s)
end

function TEST(x)
	print("\nexp",x)
	test("fp loop",x,1)
	print("fp lib",'',math.exp(x))
	test("D loop",x,D.new(1))
	print("D lib",'',D.exp(x))
end
TEST(1)
TEST(-1)
TEST(-50)

------------------------------------------------------------------------------
print""
print"Pi algorithm of order 4"

math.new=tonumber

-- http://pauillac.inria.fr/algo/bsolve/constant/pi/pi.html
function test(D)
	PI=D.new"3.1415926535897932384626433832795028841971693993751058209749445923078164062862090"
	local x=D.sqrt(2)
	local p=2+x
	local y=D.sqrt(x)
	print(-1,p)
	x=(y+1/y)/2
	p=p*(x+1)/(y+1)
	print(0,p)
	for i=1,20 do
		local P=p
		local t=D.sqrt(x)
		y=(y*t+1/t)/(y+1)
		x=(t+1/t)/2
		p=p*(x+1)/(y+1)
		print(i,p)
		if p==P then break end
	end
	print("exact",PI)
	print("-",D.abs(PI-p))
	return p
end

print"fp"
test(math)
print"D"
test(D)

------------------------------------------------------------------------------
print""
print"Square root of 2"

function mysqrt(x)
	local y,z=x,x
	repeat z,y=y,(y+x/y)/2 until z==y
	return y
end

print("fp math",math.sqrt(2))
print("fp mine",mysqrt(2))
a=D.sqrt(2) print("D sqrt",a)
b=mysqrt(D.new(2)) print("D mine",b)
R=D.new"1.414213562373095048801688724209698078569671875376948073176679737990732478462107038850387534327641573"
print("exact",R)

------------------------------------------------------------------------------
print""
print"Is exp(pi*sqrt(163)) an integer?"

a=math.exp(math.pi*math.sqrt(163))
print("fp",string.format("%.38f",a),"\nfrac",a-math.floor(a))
a=D.exp(D.pi*D.sqrt(163))
print("D",a,"\nfrac",a-D.floor(a))
R=D.new"2.625374126407687439999999999992500725971981856888793538563373369908627075374103782106479101186073129511813461e17"
print("exact",R)

------------------------------------------------------------------------------
print""
print(D.version)
