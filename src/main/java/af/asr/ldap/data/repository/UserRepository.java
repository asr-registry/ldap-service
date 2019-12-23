package af.asr.ldap.data.repository;

import af.asr.ldap.data.model.User;
import org.springframework.data.ldap.repository.LdapRepository;

import java.util.List;

public interface UserRepository extends LdapRepository<User> {
    User findByEmployeeNumber(int employeeNumber);
    List<User> findByFullNameContains(String name);
}