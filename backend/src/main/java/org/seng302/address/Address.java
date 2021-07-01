/**
 * Summary. Contains the class definition for the address JPA entity object.
 *
 * Description. Contains the class for the address JPA entity. This is used to perform
 * actions associated with address information.
 *
 * @link   team-400/backend/src/main/java/org/seng302/address/Address
 * @file   Defines the Address class.
 * @author Team-400
 * @since  5.5.2021
 */
package org.seng302.address;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.seng302.business.Business;
import org.seng302.user.User;
import org.seng302.validation.AddressValidation;

import javax.persistence.*;
import java.util.List;

/**
 * Class for Addresses.
 */
@Embeddable
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
public class Address {

    @Id // this field (attribute) is the table primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement the ID
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "street_number")
    private String streetNumber;

    @Column(name = "street_name")
    private String streetName;

    @Column(name = "city")
    private String city;

    @Column(name = "region")
    private String region;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "postcode")
    private String postcode;

    @Column(name = "suburb")
    private String suburb;

    @OneToMany(mappedBy = "homeAddress", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<User> users;

    @OneToMany(mappedBy = "address", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<Business> businesses;

    /**
     * Address constructor.
     * @param streetNumber Street Number (optional)
     * @param streetName Street Name (optional)
     * @param city City (optional)
     * @param region Region (optional)
     * @param country Country (mandatory)
     * @param postcode Postcode (optional)
     * @param suburb Suburb (optional)
     * @throws Exception Validation exception
     */
    public Address(String streetNumber,
                   String streetName,
                   String city,
                   String region,
                   String country,
                   String postcode,
                   String suburb) throws Exception {
        if (!AddressValidation.isValidStreetNumber(streetNumber)) {
            throw new Exception("Invalid street number");
        }
        if (!AddressValidation.isValidStreetName(streetName)) {
            throw new Exception("Invalid street name");
        }
        if (!AddressValidation.isValidCity(city)) {
            throw new Exception("Invalid city");
        }
        if (!AddressValidation.isValidRegion(region)) {
            throw new Exception("Invalid region");
        }
        if (!AddressValidation.isValidCountry(country)) {
            throw new Exception("Invalid country");
        }
        if (!AddressValidation.isValidPostcode(postcode)) {
            throw new Exception("Invalid postcode");
        }
        if (!AddressValidation.isValidSuburb(suburb)) {
            throw new Exception("Invalid suburb");
        }

        this.streetNumber = (streetNumber.equals("")) ? null : streetNumber;
        this.streetName = (streetName.equals("")) ? null : streetName;
        this.city = (city.equals("")) ? null : city;
        this.region = (region.equals("")) ? null : region;
        this.country = country;
        this.postcode = (postcode.equals("")) ? null : postcode;
        this.suburb = (suburb.equals("")) ? null : suburb;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public String getStreetName() {
        return streetName;
    }

    public String getCity() {
        return city;
    }

    public String getRegion() {
        return region;
    }

    public String getCountry() {
        return country;
    }

    public String getPostcode() {
        return postcode;
    }

    /**
     * Returns the suburb
     * @return suburb, the suburb corresponding to this Address.
     */
    public String getSuburb() { return suburb; }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    /**
     * Sets the suburb of this address to the incoming suburb.
     * @param suburb incoming suburb.
     */
    public void setSuburb (String suburb) {
        this.suburb = suburb;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<User> getUsers() {
        return this.users;
    }

    public void addUser(User user) {
        this.users.add(user);
        user.setHomeAddress(this);
    }

    public void removeUser(User user) {
        this.users.remove(user);
        user.setHomeAddress(null);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Business> getBusinesses() {
        return businesses;
    }

    public void setBusinesses(List<Business> businesses) {
        this.businesses = businesses;
    }

    public void addBusiness(Business business) {
        this.businesses.add(business);
        business.setAddress(this);
    }

    public void removeBusiness(Business business) {
        this.businesses.remove(business);
        business.setAddress(null);
    }

    /**
     * make a string address to become an address object
     * @param string address in json form
     * @return an address object
     */
    public static Address toAddress(String string) throws Exception {
        String[] infos = string.replace("{", "").replace("}", "").
                replace("\"", "").replace("\n","").split(",");

        String streetNumber = "";
        String streetName = "";
        String city = "";
        String region = "";
        String country = "";
        String postcode = "";
        String suburb = "";

        for (int i = 0; i < 7; i++){
            String data = infos[i].split(":")[1];
            if (i == 0) streetNumber = data;
            else if (i == 1) streetName = data;
            else if (i == 2) city = data;
            else if (i == 3) region = data;
            else if (i == 4) country = data;
            else if (i == 5) postcode = data;
            else if (i == 6) suburb = data;
        }

        return new Address(streetNumber, streetName, city, region, country, postcode, suburb);
    }

    /**
     * Convert an Address object into an AddressPayload
     * @return an AddressPayload object
     */
    public AddressPayload toAddressPayload() throws Exception {
        return new AddressPayload(
                this.streetNumber,
                this.streetName,
                this.city,
                this.region,
                this.country,
                this.postcode,
                this.suburb
        );
    }

    /**
     * Convert an Address object into an AddressPayloadSecure i.e. no street number, street name or post code
     * @return an AddressPayloadSecure object
     */
    public AddressPayloadSecure toAddressPayloadSecure() throws Exception {
        return new AddressPayloadSecure(
                this.suburb,
                this.city,
                this.region,
                this.country
        );
    }

    /**
     * Make an address object to JSON string form.
     * @return a string contain address info in JSON form
     */
    @Override
    public String toString() {
        return "{" +
                "\"streetNumber\":\"" + streetNumber + "\"," +
                "\"streetName\":\""   + streetName   + "\"," +
                "\"city\":\""         + city         + "\"," +
                "\"region\":\""       + region       + "\"," +
                "\"country\":\""      + country      + "\"," +
                "\"postcode\":\""     + postcode     + "\"," +
                "\"suburb\":\""       + suburb       + "\""  +
                "}";
    }

    /**
     * Make an address object to secure JSON string form.
     * @return a string containing only some address info in JSON form (if you are not that user)
     */

    public String toSecureString() {
        return "{" +
                "\"suburb\":\""        + suburb         + "\"," +
                "\"city\":\""        + city         + "\"," +
                "\"region\":\""       + region       + "\"," +
                "\"country\":\""      + country      + "\"" +
                "}";
    }

    /**
     * Return the object represented in a single line readable string form
     * @return a string containing address info in string form
     */
    public String toOneLineString() {
        return streetNumber + ", "
                + streetName + ", "
                + city + ", "
                + region + ", "
                + country + ", "
                + postcode + ", "
                + suburb;
    }

}
