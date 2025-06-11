package sia.tacocloud.security; // Or your chosen package

import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;
import sia.tacocloud.User;

@Data
public class RegistrationForm {
    private String username;
    private String password; // Raw password
    private String fullname;
    private String street;
    private String city;
    private String state;
    private String zip;
    private String phoneNumber;

    public User toUser(PasswordEncoder passwordEncoder) {
        return new User(
                username,
                passwordEncoder.encode(password), // Encode the password
                fullname, street, city, state, zip, phoneNumber
        );
    }
}