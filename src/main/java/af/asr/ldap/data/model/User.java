package af.asr.ldap.data.model;


import lombok.*;
import org.springframework.ldap.odm.annotations.*;
import org.springframework.ldap.support.LdapUtils;

import javax.naming.Name;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Entry(objectClasses = {"inetOrgPerson", "organizationalPerson", "person", "top"}, base = "ou=Departments")
public final class User {
    @Id
    private Name id;

    @Attribute(name = "cn")
//  @DnAttribute(value = "cn", index = 3)
    private String fullName;

    @Attribute(name = "employeeNumber")
    private int employeeNumber;

    @Attribute(name = "givenName")
    private String firstName;

    @Attribute(name = "sn")
    private String lastName;

    @Attribute(name = "title")
    private String title;

    @Attribute(name = "mail")
    private String email;

    @Attribute(name = "telephoneNumber")
    private String phone;

    //  @DnAttribute(value = "ou", index = 2)
    @Transient
    private String unit;

    //  @DnAttribute(value = "ou", index = 1)
    @Transient
    private String department;


}