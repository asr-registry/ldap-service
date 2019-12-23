package af.asr.ldap.resource;

import af.asr.ldap.data.model.User;
import af.asr.ldap.data.service.UserService;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.xml.ws.Response;
import java.util.Date;
import java.util.Map;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/users")
public class UserApi {

    @Autowired
    private UserService userService;

    @PostMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getUserLogin(Authentication principal) {
        Map<String, Object> params = new HashedMap<>();
        params.put("time", new Date());
        params.put("currentUser", principal.getName());
        return ok().body(params);
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<Map<String , Object>> findAllUsers()
    {
        Map<String , Object> data = new HashedMap<>();
        data.put("users", userService.findAll());

        return ResponseEntity.ok(data);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public User createUser(User user) {
        User savedUser = userService.createUser(user);
        return savedUser;
    }
}
