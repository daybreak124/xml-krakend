local function urldecode(str)
	if str == nil then return str end
	str = string.gsub(str, "+", " ")
	str = string.gsub(str, "%%(%x%x)", function(h)
		return string.char(tonumber(h, 16))
	end)
	return str
end

function split(str)
	local result = {}
	local decoded = urldecode(str)
	for token in string.gmatch(decoded, "[^,]+") do
		result[string.lower(token)] = true
	end
	return result
end

-- child bir "table" (obje/array) mi yoksa scalar mı, keys() cagirip anliyoruz
function isTable(v)
	if v == nil then return false end
	local ok = pcall(function() return v:keys() end)
	return ok
end

function tableLen(v)
	local ok, keys = pcall(function() return v:keys() end)
	if not ok then return 0 end
	return keys:len()
end

-- GENEL KURAL:
--   - key'in degeri bir table/obje/array ise -> once icine recurse edilir.
--     Recurse sonrasi o table BOS kaldiysa VE key allowed listesinde
--     acikca istenmemisse -> key tamamen silinir (permissions, NumberToWordsResponse vb.)
--     Boylece hem cok-kollu wrapper'lar hem tek-kollu wrapper'lar, icinde
--     istenen bir field kaldigi surece korunur; hicbir sey kalmadiysa kaybolur.
--   - key'in degeri scalar (string/number) ise -> allowed listesinde yoksa silinir.
--   - array index'leri (0,1,2..) field adi olmadigi icin hep korunur, icine recurse edilir.
function filterRecursive(node, allowed)
	if node == nil then return end
	local ok, keys = pcall(function() return node:keys() end)
	if not ok then return end

	for i = 0, keys:len() - 1 do
		local key = tostring(keys:get(i))
		local child = node:get(key)
		local isArrayIndex = string.match(key, "^%d+$") ~= nil

		if isArrayIndex then
			filterRecursive(child, allowed)
		elseif isTable(child) then
			filterRecursive(child, allowed)
			if tableLen(child) == 0 and not allowed[string.lower(key)] then
				node:del(key)
			end
		elseif not allowed[string.lower(key)] then
			node:del(key)
		end
	end
end

function filterFields(req, resp)
	local query = req:query()
	local fieldsParam = string.match(query, "fields=([^&]*)")
	if fieldsParam == nil or fieldsParam == "" then
		return
	end

	local allowed = split(fieldsParam)
	local data = resp:data()

	filterRecursive(data, allowed)
end