package sql.auth.security;

import java.util.List;

public record AuthenticatedUser(Integer userId, List<String> roles) {
}
