package inventory.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class User {
    public String checkMailValueSet(String mail) throws Exception {
        try {
            String[] constraints = new String[2];
            constraints[0] ="@";
            constraints[1] = "\\.";
            String regex = "^[A-Za-z0-9._%+-]+"+constraints[0]+"[A-Za-z0-9.-]+"+constraints[1]+"[A-Za-z]{2,}$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(mail);
            if (matcher.matches()) {
                return mail;
            }
            else {
                throw new Exception("Format de mail invalide.");
            }
        } catch(Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public String hashPassword(String pwd) throws Exception {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(pwd.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Echec de hash de mot de passe: "+ e);
        }
    }
}
