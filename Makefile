all:
	javac -d resources src/org/ut/biolab/medsavant/*.java

clean:
	-rm -r resources/org/ut/biolab/medsavant/*.class
