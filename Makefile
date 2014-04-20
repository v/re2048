CLASSPATH=${HOME}/code/re2048/libs/neuroph-core-2.8.jar
all:
	javac -cp ${CLASSPATH} *.java

clean:
	rm *.class
