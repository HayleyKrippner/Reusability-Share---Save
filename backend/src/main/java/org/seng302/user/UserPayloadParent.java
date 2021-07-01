/**
 * Summary. This file contains the definition for the UserPayloadParent.
 *
 * Description. This file contains the defintion for the UserPayloadParent.
 *
 * @link   team-400/src/main/java/org/seng302/user/UserPayloadParent
 * @file   This file contains the definition for UserPayloadParent.
 * @author team-400.
 * @since  5.5.2021
 */
package org.seng302.user;

import org.seng302.business.Business;
import org.seng302.business.BusinessPayload;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Payload for the User (notably excluding the password field, for JSON responses).
 */
abstract class UserPayloadParent {
    private int id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String nickname;
    private String bio;
    private String email;
    private String created;
    private Role role;
    private List<BusinessPayload> businessesAdministered;


    public UserPayloadParent (
            int id,
            String firstName,
            String lastName,
            String middleName,
            String nickname,
            String bio,
            String email,
            LocalDateTime created,
            Role role,
            List<Business> businessesAdministeredObject
    ) throws Exception {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.nickname = nickname;
        this.bio = bio;
        this.email = email;
        this.created = created.toString();
        this.role = role;
        this.businessesAdministered = BusinessPayload.toBusinessPayload(businessesAdministeredObject);
        if (this.businessesAdministered.isEmpty()){
            this.businessesAdministered.add(null);
        }
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getNickname() {
        return nickname;
    }

    public String getBio() {
        return bio;
    }

    public String getEmail() {
        return email;
    }

    public String getCreated() {
        return created;
    }

    public Role getRole() {
        return role;
    }

    public List<BusinessPayload> getBusinessesAdministered() {
        return businessesAdministered;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setBusinessesAdministered(List<BusinessPayload> businessesAdministered) {
        this.businessesAdministered = businessesAdministered;
    }
}
