function cleanAndroidGen()
{
	local base=$1
	echo "cleanAndroidGen(${base})"
	rm -rfv ${base}
}

cleanAndroidGen build
cleanAndroidGen app/build
cleanAndroidGen app/.cxx
