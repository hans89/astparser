CC = javac
EC = java

ECLIPSEJDT = lib/org.eclipse.core.contenttype_3.4.200.v20130326-1255.jar:lib/org.eclipse.core.jobs_3.5.300.v20130429-1813.jar:lib/org.eclipse.core.resources_3.8.101.v20130717-0806.jar:lib/org.eclipse.core.runtime_3.9.0.v20130326-1255.jar:lib/org.eclipse.equinox.common_3.6.200.v20130402-1505.jar:lib/org.eclipse.equinox.preferences_3.5.100.v20130422-1538.jar:lib/org.eclipse.jdt.core_3.9.1.v20130905-0837.jar:lib/org.eclipse.osgi_3.9.1.v20130814-1242.jar


JARPATH = $(ECLIPSEJDT)
JUNIT = lib/junit/junit-4.11.jar:lib/junit/hamcrest-core-1.3.jar
CLASSOUT = classes
SRCPATH = .
CLASSPATH = $(SRCPATH):$(JARPATH):$(JUNIT):$(CLASSOUT)

SOURCES = *.java
PKGNAME = astparser
MAINCLASS = $(PKGNAME).Main
TESTCLASS = $(PKGNAME).TestSuit
TESTRUNNER = org.junit.runner.JUnitCore 

all:
	mkdir -p $(CLASSOUT)
	$(CC) -sourcepath $(SRCPATH) -classpath $(CLASSPATH) $(SOURCES) -d $(CLASSOUT)

test: all
		$(EC) -cp $(CLASSPATH) $(TESTRUNNER) $(TESTCLASS)

main: all
		$(EC) -cp $(CLASSPATH) $(MAINCLASS)

clean:
	rm -rf $(CLASSOUT)