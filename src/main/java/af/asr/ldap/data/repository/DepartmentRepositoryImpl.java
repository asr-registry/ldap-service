package af.asr.ldap.data.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.NameClassPairMapper;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Component;

import javax.naming.NameClassPair;
import javax.naming.NamingException;
import javax.naming.ldap.LdapName;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class DepartmentRepositoryImpl implements DepartmentRepository {

    private static final LdapName DEPARTMENTS_OU = LdapUtils.newLdapName("ou=Departments");

    private LdapTemplate ldapTemplate;

    @Autowired
    public DepartmentRepositoryImpl(LdapTemplate ldapTemplate) {
        this.ldapTemplate = ldapTemplate;
    }

    public DepartmentRepositoryImpl() {
    }

    @Override
    public Map<String, List<String>> getDepartmentMap() {
        return new HashMap<String, List<String>>() {{
            List<String> allDepartments = getAllDepartments();
            for (String oneDepartment : allDepartments) {
                put(oneDepartment, getAllUnitsForDepartment(oneDepartment));
            }
        }};
    }

    private List<String> getAllDepartments() {
        return ldapTemplate.list(DEPARTMENTS_OU, new OuValueNameClassPairMapper());
    }

    private List<String> getAllUnitsForDepartment(String department) {
        return ldapTemplate.list(LdapNameBuilder.newInstance(DEPARTMENTS_OU).add("ou", department).build(),
                new OuValueNameClassPairMapper());
    }

    private static class OuValueNameClassPairMapper implements NameClassPairMapper<String> {
        @Override
        public String mapFromNameClassPair(NameClassPair nameClassPair) throws NamingException {
            LdapName name = LdapUtils.newLdapName(nameClassPair.getName());
            return LdapUtils.getStringValue(name, "ou");
        }
    }
}