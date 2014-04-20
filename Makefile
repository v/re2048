CLASSPATH=${HOME}/code/re2048/libs/encog-core-3.1.0.jar:.
all:
	javac -cp ${CLASSPATH} *.java

train:
	java -cp ${CLASSPATH} NNLearn

test:
	java -cp ${CLASSPATH} Exploit

clean:
	rm -f *.class
