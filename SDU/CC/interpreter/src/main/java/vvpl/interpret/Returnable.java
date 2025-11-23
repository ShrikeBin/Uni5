package vvpl.interpret;

public class Returnable extends RuntimeException
{
    final Object value;
    public Returnable(Object value)
    {
        // turining off stack trace for performance
        super(null, null, false, false);
        this.value = value;
    }
}
