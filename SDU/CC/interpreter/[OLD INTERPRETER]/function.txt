package vvpl.interpret;

import java.util.List;

import vvpl.ast.Statement;
import vvpl.ast.function.Param;
import vvpl.ast.statement.Block;
import vvpl.errors.SyntaxError;
import vvpl.errors.TypeError;
import vvpl.scan.TokenType;

public class Function 
{
    boolean allowAccesToGlobalVariables = false;

    private final String name;
    private final List<Param> params;
    private final String type;
    private final Statement body;

    public Function(String name, List<Param> params, String type, Statement body) 
    { 
        this.name = name; 
        this.params = params;
        this.body = body;
        this.type = type;

        if(!(body instanceof Block))
        {
            throw new SyntaxError(name + " Function body must be a block statement.");
        }
    }

    public Object call(Interpreter interpreter, List<Object> args) 
    {
        // create new environment for function scope
        Environment functionEnv = new Environment(null);

        if(allowAccesToGlobalVariables)
        {
            functionEnv = new Environment(interpreter.env);
        }
        else
        {
            for (Function func : interpreter.env.getFunctions()) 
            {
                functionEnv.put(func.name, func);
            } 
        }

        // ==== Check and Bind parameters ====
        for (int i = 0; i < params.size(); i++) 
        {
            String paramName = params.get(i).name.lexeme;
            String paramType = getTypeString(params.get(i).type.type);
            Object argValue = args.get(i);
            if(!typeMatch(argValue, paramType))
            {
                throw new TypeError("Incorrect function parameter in call for '" + name + "' for argument: " + paramName);
            }
            functionEnv.put(paramName, argValue);
        }

        boolean prev = interpreter.inFunction;
        Environment prevEnv = interpreter.env;
        interpreter.env = functionEnv;
        interpreter.inFunction = true;

        try
        {
            interpreter.visitBlockStmt((Block) body);
        } 
        catch (Returnable ret) 
        {
            if(ret.value == null && !type.equals("void"))
            {
                throw new TypeError(name + " returned unexpected null");
            }
            else if(!typeMatch(ret.value, type))
            {
                throw new TypeError(name + ", " + type + " returned unexpected type: " + ret.value.getClass().getSimpleName().toLowerCase());
            }
            return ret.value;
        }
        finally
        {
            interpreter.env = prevEnv;
            interpreter.inFunction = prev;
        }
        return null;
    }

    private String getTypeString(TokenType type)
    {
        switch(type)
        {
            case NUMBER_TYPE:
                return "number";
            case STRING_TYPE:
                return "string";
            case BOOL_TYPE:
                return "boolean";
            case FUNCTION:
                return "function";
            default:
                return "unknown";
        }
    }

     private boolean typeMatch(Object value, String expectedType) 
    {
        if (value == null) return false;

        switch (expectedType.toLowerCase()) 
        {
            case "number":
                return value instanceof Double || value instanceof Integer;
            case "integer":
                return value instanceof Integer;
            case "double":
                return value instanceof Double;
            case "string":
                return value instanceof String;
            case "boolean":
                return value instanceof Boolean;
            case "function":
                return value instanceof Function;
            case "void":
                return value == null;
            default:
                return false;
        }
    }
}
