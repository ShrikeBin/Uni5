import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

import vvpl.scan.Scanner;
import vvpl.scan.Token;
import vvpl.ast.Declaration;
import vvpl.ast.visitors.ASTPrinter;
import vvpl.parse.*;
import vvpl.errors.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParserTest
{
    private static List<Path> inputFiles;

    @BeforeAll
    public static void setup() throws IOException 
    {
        ErrorHandler.setPrintsOff();
        Path dir = Paths.get("src/test/resources/parse");
        try (Stream<Path> stream = Files.list(dir)) 
        {
            inputFiles = stream
                        .filter(p -> p.toString().endsWith(".in"))
                        .sorted()
                        .collect(Collectors.toList());
        }
        catch (IOException e) 
        {
            throw new IOException("Failed to list test resources in " + dir.toString(), e);
        }
    }

    private String generate(String source) 
    {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        if(ErrorHandler.errors.size() != 0)
		{
            ErrorHandler.flush();
            return "";
		}

        Parser parser = new Parser(tokens);
        List<Declaration> program = parser.parse();

        if(ErrorHandler.errors.size() != 0)
		{
            String out = ErrorHandler.getErrors();
            ErrorHandler.flush();
            return out;
		}
        else
        {
            ErrorHandler.flush();
            return new ASTPrinter().print(program);
        }
    }

    @TestFactory
    public List<DynamicTest> generateTests() 
    {
        // Yes we are doing 'functional' Java here :)
        return inputFiles.stream().map(inputPath -> 
        {
            String testName = inputPath.getFileName().toString();
            Path expectedPath = Paths.get(inputPath.toString().replace(".in", ".parse"));

            return DynamicTest.dynamicTest("l-LLVM translation test for " + testName, () -> 
            {
                String input = Files.readString(inputPath);
                String expectedOut = Files.readString(expectedPath);
                String actualOutput = generate(input);

                // if \n doesnt work use \\R -> matches all line terminators
                List<String> expectedLines = Arrays.asList(expectedOut.split("\n"));
                List<String> actualLines = Arrays.asList(actualOutput.split("\n"));

                assertTrue(expectedLines.size() == actualLines.size(), "Line count mismatch in " + testName);

                for (int i = 0; i < expectedLines.size(); i++) 
                {
                    String expected = expectedLines.get(i).trim();
                    String actual = actualLines.get(i).trim();
                    assertTrue(expected.equals(actual),
                            "Mismatch in " + testName + 
                            " line " + (i + 1) +
                            "\nExpected: " + expected +
                            "\nGot:      " + actual);
                }
            });
        }).collect(Collectors.toList());
    }
}
