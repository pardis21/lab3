.PHONY: all, help, clean

JC = javac
JE = java
JCP = -classpath .
JOPT = -d . -sourcepath .

SOURCES = ${wildcard *.java}
CLASSES = ${SOURCES:%.java=%.class}
EXEC = Main

all : $(CLASSES)
	@echo "Whole project has been compilated"

%.class : %.java
	@echo "Compilation of <$<>"
	@$(JC) $(JOPT) $<

clean :
	@echo "Delete .class files."
	@rm *.class -vf

run_1 :
	@$(JE) $(JCP) $(EXEC) 1 $(N)
run_2 :
	@$(JE) $(JCP) $(EXEC) 2 $(N)
run_3 :
	@$(JE) $(JCP) $(EXEC) 3 $(N)
run_4 :
	@$(JE) $(JCP) $(EXEC) 4 $(N)
help :
	@echo "============================================================"
	@echo "Compilation of application :"
	@echo "     make"
	@echo "============================================================"
	@echo "Delete .class files :"
	@echo "     make clean"
	@echo "============================================================"
	@echo "Run application :"
	@echo "     make run "
	@echo "============================================================"
