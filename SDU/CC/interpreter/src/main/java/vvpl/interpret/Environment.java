package vvpl.interpret;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import vvpl.errors.*;

public class Environment 
{
    private final Environment parent;
    public final Map<String, Object> scope = new HashMap<>();

    public Environment(Environment parent)
    {
        this.parent = parent;
    }

    public void set(String name, Object obj)
    {
        if(this.scope.get(name) != null)
        {
            this.scope.put(name, obj);
            return;
        }
        else if(parent != null)
        {
            parent.set(name, obj);
            return;
        }
        else
        {
            throw new ScopeError(name + " is not defined in the current scope.");
        }
    }

    public void put(String name, Object obj)
    {
        if(this.scope.get(name) == null)
        {
            this.scope.put(name, obj);
            return;
        }
        else
        {
            throw new ScopeError(name + " is already defined in the current scope.");
        }
    }

    public Object get(String name)
    {
        Object obj = scope.get(name);
        if(obj == null && parent != null)
        {
            return parent.get(name);
        }
        else
        {
            return obj;
        }
    }

    public List<Function> getFunctions()
    {
        List<Function> functions = new ArrayList<>();
        for(Map.Entry<String, Object> entry : scope.entrySet())
        {
            if(entry.getValue() instanceof Function)
            {
                functions.add((Function) entry.getValue());
            }
        }
        if(parent != null)
        {
            functions.addAll(parent.getFunctions());
        }
        return functions;
    }
}
