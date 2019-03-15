package crypto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PasswordTest extends CryptoTestBase {
    @Test
    public void smallStringHashTest() throws Exception {
        String text = "smallStringTest";
        String expected = PasswordUtil.hash(text);
        String actual = PasswordUtil.hash(text);

        assertEquals(expected, actual);
    }

    @Test
    public void smallStringHashSaltTest() throws Exception {
        String text = "smallStringTest";
        HashSalt expected = PasswordUtil.hash(text, null);
        HashSalt actual = PasswordUtil.hash(text, expected);

        assertEquals(expected.equals(actual), true);
    }
}
