package academy.learnprogramming.security;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.shiro.codec.Hex;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha512Hash;
import org.apache.shiro.util.ByteSource;

import academy.learnprogramming.entity.User;
import academy.learnprogramming.service.UserService;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;

@ApplicationScoped
public class SecurityUtil {

  public static final String HASHED_PASSWORD_KEY = "hashedPassword";
  public static final String SALT_KEY = "salt";
  public static final String BEARER = "Bearer";
  private SecretKey securityKey;

  @Inject
  UserService userService;

  @PostConstruct
  private void init() {
    this.securityKey = generateKey();
  }

  public Date toDate(LocalDateTime localDateTime) {
    return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
  }

  public boolean passwordsMatch(String dbStoredPassword, String dbStoredSalt, String clearTextPassword) {
    ByteSource salt = ByteSource.Util.bytes(Hex.decode(dbStoredSalt));
    String hashedPassword = hashPassword(clearTextPassword, salt);

    return hashedPassword.equals(dbStoredPassword);
  }
  
  public Map<String, String> getHashedPassword(String clearTextPassword) {
    ByteSource salt = getSalt();
    
    Map<String, String> credMap = new HashMap<>();
    credMap.put(HASHED_PASSWORD_KEY, hashPassword(clearTextPassword, salt));
    credMap.put(SALT_KEY, salt.toHex());
    return credMap;    
  }

  private String hashPassword(String clearTextPassword, ByteSource salt) {
    return new Sha512Hash(clearTextPassword, salt, 1000000).toHex();
  }

  private ByteSource getSalt() {
    return new SecureRandomNumberGenerator().nextBytes();
  }

  public boolean authenticateUser(String email, String password) {
    User user = userService.findUserByEmail(email);

    if(user == null) {
      return false;
    }
    return passwordsMatch(user.getPassword(), user.getSalt(), password);
  }

  private SecretKey generateKey() {
    return MacProvider.generateKey(SignatureAlgorithm.HS512);
  }

  public SecretKey getSecurityKey() {
    return this.securityKey;
  }
}
