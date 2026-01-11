package Utilities.Security.Password;

public class PasswordStats {

    private int length;
    private int lowercase;
    private int uppercase;
    private int digits;
    private int symbols;

    public PasswordStats(String password) {
        this.length = password.length();
        this.lowercase = 0;
        this.uppercase = 0;
        this.digits = 0;
        this.symbols = 0;

        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) lowercase++;
            else if (Character.isUpperCase(c)) uppercase++;
            else if (Character.isDigit(c)) digits++;
            else symbols++;
        }
    }

    public int getLength() { return length; }
    public int getLowercase() { return lowercase; }
    public int getUppercase() { return uppercase; }
    public int getDigits() { return digits; }
    public int getSymbols() { return symbols; }
}