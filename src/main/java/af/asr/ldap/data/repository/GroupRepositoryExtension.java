package af.asr.ldap.data.repository;

import af.asr.ldap.data.model.Group;

import java.util.List;

public interface GroupRepositoryExtension {
    List<String> getAllGroupNames();

    void create(Group group);
}