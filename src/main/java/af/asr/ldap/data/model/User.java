package af.asr.ldap.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ldap.odm.annotations.Entry;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entry(base="ou=people", objectClasses = {"inetOrgPerson", "person", "top", "inetOrgPerson"})
public class User {

//    private Name

}
