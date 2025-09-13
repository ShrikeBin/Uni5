# Compilation output directory
BIN_DIR = bin

# Classpath for runtime
CLASS_PATH = $(BIN_DIR)

# Targets
all:
	mkdir -p $(BIN_DIR)
	javac -d $(BIN_DIR) Spl.java

clean:
	rm -rf $(BIN_DIR)

run:
	java -cp $(CLASS_PATH) Spl $(ARGS)
