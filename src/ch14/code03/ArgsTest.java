package ch14.code03;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArgsTest {

    @Test
    public void testCreateWithNoSchemaOrArguments() throws Exception{
        Args args = new Args("", new String[0]);
        assertEquals(0, args.cardinality());
    }

    @Test
    public void testSimpleBooleanPresent() throws Exception{
        Args args = new Args("x", new String[]{"-x"});
        assertEquals(1, args.cardinality());
        assertTrue(args.getBoolean('x'));
    }

    @Test
    public void testSimpleStringPresent() throws Exception{
        Args args = new Args("x*", new String[]{"-x","param"});
        assertEquals(1, args.cardinality());
        assertTrue(args.has('x'));
        assertEquals("param", args.getString('x'));
    }

}
