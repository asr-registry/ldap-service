package af.asr.ldap.data.service;

import af.asr.ldap.data.model.DirectoryType;
import af.asr.ldap.data.model.Group;
import af.asr.ldap.data.model.User;
import af.asr.ldap.data.repository.GroupRepository;
import af.asr.ldap.data.repository.UserRepository;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.support.BaseLdapNameAware;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Component;

import javax.naming.Name;
import javax.naming.ldap.LdapName;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class UserService implements BaseLdapNameAware {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private GroupRepository groupRepo;

    private static Logger LOG = LoggerFactory.getLogger(UserService.class);

    private LdapName baseLdapPath;

    private DirectoryType directoryType;

    @Autowired
    public UserService(UserRepository userRepo, GroupRepository groupRepo) {
        LOG.info("UserService instanciated");
        this.userRepo = userRepo;
        this.groupRepo = groupRepo;
    }

    public Group getUserGroup() {
        LOG.info("getUserGroup");
        return groupRepo.findByName(GroupRepository.USER_GROUP);
    }

    public void setDirectoryType(DirectoryType directoryType) {
        this.directoryType = directoryType;
    }

    @Override
    public void setBaseLdapPath(LdapName baseLdapPath) {
        this.baseLdapPath = baseLdapPath;
    }

    public Iterable<User> findAll() {
        LOG.info("findAll");
        return userRepo.findAll();
    }

    public User findUser(String userId) {
        LOG.info("findUser " + userId);
        return userRepo.findById(LdapUtils.newLdapName(userId)).get();
    }

    public User createUser(User user) {
        User savedUser = userRepo.save(user);

        Group userGroup = getUserGroup();

        // The DN the member attribute must be absolute
        userGroup.addMember(toAbsoluteDn(savedUser.getId()));
        groupRepo.save(userGroup);

        return savedUser;
    }

    public LdapName toAbsoluteDn(Name relativeName) {
        return LdapNameBuilder.newInstance(baseLdapPath)
                .add(relativeName)
                .build();
    }

    /**
     * This method expects absolute DNs of group members. In order to find the actual users
     * the DNs need to have the base LDAP path removed.
     *
     * @param absoluteIds
     * @return
     */
    public Set<User> findAllMembers(Iterable<Name> absoluteIds) {
        LOG.info("findAllMembers");
        return Sets.newLinkedHashSet(userRepo.findAll());
    }

    public Iterable<Name> toRelativeIds(Iterable<Name> absoluteIds) {
        return Iterables.transform(absoluteIds, new Function<Name, Name>() {
            @Override
            public Name apply(Name input) {
                return LdapUtils.removeFirst(input, baseLdapPath);
            }
        });
    }

    public User updateUser(String userId, User user) {
        LdapName originalId = LdapUtils.newLdapName(userId);
        User existingUser = userRepo.findById(originalId).get();

        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setFullName(user.getFullName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());
        existingUser.setTitle(user.getTitle());
        existingUser.setDepartment(user.getDepartment());
        existingUser.setUnit(user.getUnit());

        if (directoryType == DirectoryType.AD) {
            return updateUserAd(originalId, existingUser);
        } else {
            return updateUserStandard(originalId, existingUser);
        }
    }


    /**
     * Update the user and - if its id changed - update all group references to the user.
     *
     * @param originalId   the original id of the user.
     * @param existingUser the user, populated with new data
     * @return the updated entry
     */
    private User updateUserStandard(LdapName originalId, User existingUser) {
        User savedUser = userRepo.save(existingUser);

        if (!originalId.equals(savedUser.getId())) {
            // The user has moved - we need to update group references.
            LdapName oldMemberDn = toAbsoluteDn(originalId);
            LdapName newMemberDn = toAbsoluteDn(savedUser.getId());

            Collection<Group> groups = groupRepo.findByMembers(oldMemberDn);
            updateGroupReferences(groups, oldMemberDn, newMemberDn);
        }
        return savedUser;
    }

    /**
     * Special behaviour in AD forces us to get the group membership before the user is updated,
     * because AD clears group membership for removed entries, which means that once the user is
     * update we've lost track of which groups the user was originally member of, preventing us to
     * update the membership references so that they point to the new DN of the user.
     * <p/>
     * This is slightly less efficient, since we need to get the group membership for all updates
     * even though the user may not have been moved. Using our knowledge of which attributes are
     * part of the distinguished name we can do this more efficiently if we are implementing specifically
     * for Active Directory - this approach is just to highlight this quite significant difference.
     *
     * @param originalId   the original id of the user.
     * @param existingUser the user, populated with new data
     * @return the updated entry
     */
    private User updateUserAd(LdapName originalId, User existingUser) {
        LdapName oldMemberDn = toAbsoluteDn(originalId);
        Collection<Group> groups = groupRepo.findByMembers(oldMemberDn);

        User savedUser = userRepo.save(existingUser);
        LdapName newMemberDn = toAbsoluteDn(savedUser.getId());

        if (!originalId.equals(savedUser.getId())) {
            // The user has moved - we need to update group references.
            updateGroupReferences(groups, oldMemberDn, newMemberDn);
        }
        return savedUser;
    }

    private void updateGroupReferences(Collection<Group> groups, Name originalId, Name newId) {
        for (Group group : groups) {
            group.removeMember(originalId);
            group.addMember(newId);

            groupRepo.save(group);
        }
    }

    public List<User> searchByNameName(String lastName) {
        LOG.info("searchByNameName " + lastName);
        return userRepo.findByFullNameContains(lastName);
    }
}