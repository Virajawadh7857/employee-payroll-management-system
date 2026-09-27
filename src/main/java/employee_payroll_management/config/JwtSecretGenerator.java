package employee_payroll_management.config;

import java.util.Base64;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Jwts;

public class JwtSecretGenerator {

    public static void main(String[] args) {

        SecretKey key = Jwts.SIG.HS256.key().build();

        String secret = Base64.getEncoder()
                .encodeToString(key.getEncoded());

        System.out.println(secret);
    }
}