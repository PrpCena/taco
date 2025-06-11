package sia.tacocloud.security;// Or your chosen package

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sia.tacocloud.User;
import sia.tacocloud.data.UserRepository;


@Service // Marks this as a Spring-managed service bean
public class UserRepositoryUserDetailsService implements UserDetailsService {

    private final UserRepository userRepo;

    @Autowired
    public UserRepositoryUserDetailsService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username);
        if (user != null) {
            return user; // Your User class implements UserDetails
        }
        throw new UsernameNotFoundException("User '" + username + "' not found");
    }
}