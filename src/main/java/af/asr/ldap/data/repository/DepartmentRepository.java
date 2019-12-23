package af.asr.ldap.data.repository;

import java.util.List;
import java.util.Map;


public interface DepartmentRepository {
    Map<String, List<String>> getDepartmentMap();
}