# Compilation output directory
BIN_DIR = bin

# Classpath for runtime
CLASS_PATH = $(BIN_DIR)

# Java sources
SOURCES = Spl.java parser/*.java scanner/*.java parser/expression/*.java parser/statement/*.java

# Targets
all:
	mkdir -p $(BIN_DIR)
	javac -d $(BIN_DIR) $(SOURCES)

clean:
	rm -rf $(BIN_DIR)

run:
	java -cp $(CLASS_PATH) spl.Spl $(ARGS)
