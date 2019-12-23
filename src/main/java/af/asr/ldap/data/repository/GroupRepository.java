package af.asr.ldap.data.repository;


import af.asr.ldap.data.model.Group;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.ldap.repository.LdapRepository;


import javax.naming.Name;
import java.util.Collection;

/**
 * Spring Data-generated repository for Group administration. The methods defined in LdapRepository
 * and its superinterfaces directly map mot {@link org.springframework.data.ldap.repository.support.SimpleLdapRepository}.
 * <p/>
 * The methods defined in {@link GroupRepositoryExtension} are implemented in the generated instance by 'weaving in' a reference
 * to a bean in the applicationContext implementing the interface.
 * <p/>
 * In the {@link #findByName(String)} method, the filter will be automatically be generated based on
 * naming convention; the 'ByName' constraint will be fulfilled using a filter based on the attribute mapping of
 * the name attribute in the target entity class.
 * <p/>
 * The {@link #findByMembers(javax.naming.Name)} acts on the Query annotation, building an
 * {@link org.springframework.ldap.query.LdapQuery} from the annotation attributes.

 */
public interface GroupRepository extends LdapRepository<Group>, GroupRepositoryExtension {
    public final static String USER_GROUP = "ROLE_USER";
    Group findByName(String groupName);
    @Query("(member={0})")
    Collection<Group> findByMembers(Name member);
}