#!/bin/bash
# Parameters : 
#              1 - Number of files generated for each input file
#              2 - Number of times each file is processed
#              3 - Target directory
#              4 - Directory for seed files
BASEDIR="$(cd $3; pwd)"
rm -rf $BASEDIR/generatedModels/tekgenesis/sales
mkdir -p $BASEDIR/generatedModels/tekgenesis/sales
cd $4
for fl in tekgenesis/sales/*.seed; do 
     	NAME=${fl%.seed}
	echo $NAME
	for ((j=1; j<=$1; j++))
	do
	     echo -e "package tekgenesis.sales;\n" > $BASEDIR/generatedModels/$NAME$j.mm
	     for ((i=1; i<=$2; i++))
	     do
		SUFFIX="${j}_$i"
		sed "s/-SUFFIX-/$SUFFIX/g" $fl >> $BASEDIR/generatedModels/$NAME$j.mm
	     done
	done
done
