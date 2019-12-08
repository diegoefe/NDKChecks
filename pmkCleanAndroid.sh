function cleanAndroidGen()
{
	local base=$1
	echo "cleanAndroidGen(${base})"
	rm -rfv ${base}
}

cleanAndroidGen app/build
cleanAndroidGen build
