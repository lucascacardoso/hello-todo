package academy.learnprogramming.entity;

import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NamedQuery(name = User.FIND_ALL_USERS, query = "select u from User u order by u.fullName")
@NamedQuery(name = User.FIND_USER_BY_EMAIL, query = "select u from User u where u.email = :email")
@NamedQuery(name = User.FIND_USER_BY_PASSWORD, query = "select u from User u where u.password = :password")

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TodoUser")
public class User extends AbstractEntity{

  public static final String FIND_ALL_USERS = "User.findAllUsers";
  public static final String FIND_USER_BY_EMAIL = "User.findUserByEmail";
  public static final String FIND_USER_BY_PASSWORD = "User.findUserByPassword";

  @NotNull(message = "Full name must not be null")
  @Pattern(regexp = "^[a-zA-Z]{2,}(?: [a-zA-Z]+){0,2}$", message = "Full name must be alphabetic")
  private String fullName;

  @NotNull(message = "Email must be set")
  @Email(message = "Email must be in the correct form")
  private String email;

  @NotNull(message = "Password must be set")
  @Size(min = 8)
  private String password;

  private String salt;
    
}
