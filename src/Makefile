JFLAGS = -g
JC = javac
JVM = java
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

# This uses the line continuation character (\) for readability
# You can list these all on a single line, separated by a space instead.
# If your version of make can't handle the leading tabs on each
# line, just remove them (these are also just added for readability).
CLASSES = \
	client\UDPClient.java \
	server\UDPServer.java \
	server\ClientLog.java \

MAIN = \
	server/UDPServer \

MAIN2 = \
	client/UDPClient -a \

default: classes

classes: $(CLASSES:.java=.class)

runServer: classes
	$(JVM) $(MAIN)

runClient1: classes
	$(JVM) $(MAIN2) "Alex"
	
runClient2: classes
	$(JVM) $(MAIN2) "Vaffa"

runClient3: classes
	$(JVM) $(MAIN2) "Lizan"

clean:
	$(RM) *.class