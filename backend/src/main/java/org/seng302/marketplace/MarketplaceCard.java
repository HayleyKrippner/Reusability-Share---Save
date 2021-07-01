package org.seng302.marketplace;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.seng302.business.Business;
import org.seng302.user.User;
import org.seng302.validation.MarketplaceCardValidation;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for a marketplace card
 */
@Embeddable
@NoArgsConstructor  // generate a no-args constructor needed by JPA (lombok pre-processor)
@Entity             // declare this class as a JPA entity (that can be mapped to a SQL table)
public class MarketplaceCard {

    @Id // this field (attribute) is the table primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement the ID
    @Column(name = "id", nullable = false)
    private int id;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER) //EAGER to allow access to this attribute outside of a context of an open hibernate session (for loading initial data SQL script)
    @JoinColumn(name = "creator_id", insertable = false, updatable = false)
    private User creator;

    @Column(name = "creator_id")
    private Integer creatorId;

    @Enumerated(EnumType.STRING)
    private Section section;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @Column(name = "display_period_end", nullable = false)
    private LocalDateTime displayPeriodEnd;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", length = 500)
    private String description;

    @JsonManagedReference
    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "card_keywords",
            joinColumns = { @JoinColumn(name = "card_id") },
            inverseJoinColumns = { @JoinColumn(name = "keyword_id") })
    private List<Keyword> keywords = new ArrayList<>();

    /**
     * Marketplace card constructor
     * @param creatorId
     * @param creator
     * @param section
     * @param created
     * @param title
     * @param description
     * @throws Exception Validation exception
     */
    public MarketplaceCard(
            int creatorId,
            User creator,
            Section section,
            LocalDateTime created,
            String title,
            String description
    ) throws Exception {
        if (!MarketplaceCardValidation.isValidTitle(title)) {
            throw new Exception("Invalid title");
        }
        if (!MarketplaceCardValidation.isValidDescription(description)) {
            throw new Exception("Invalid description");
        }

        this.creatorId = creatorId;
        this.creator = creator;
        this.section = section;
        this.created = created;
        this.displayPeriodEnd = created.plusWeeks(1);
        this.title = title;
        this.description = (description.equals("")) ? null : description;
    }

    public int getId() {
        return id;
    }

    public int getCreatorId() {
        return creatorId;
    }

    /**
     * Get the User who created this card.
     * @return creator the user that created this card.
     */
    public User getCreator() { return creator; }

    public Section getSection() {
        return section;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public LocalDateTime getDisplayPeriodEnd() {
        return displayPeriodEnd;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * Change the user who created this card.
     * @param creator the user that created this card.
     */
    public void serCreator(User creator) { this.creator = creator; }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public void setDisplayPeriodEnd(LocalDateTime displayPeriodEnd) {
        this.displayPeriodEnd = displayPeriodEnd;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns a list of keywords that the card contains.
     * @return keywords a list of keyword entities.
     */
    public List<Keyword> getKeywords() {
        return keywords;
    }

    /**
     * Add a keyword to the list of keywords for this card.
     * Called when a new keyword is created.
     * @return keyword a keyword that is to be added to this card.
     */
    public void addKeyword(Keyword keyword) {
        keywords.add(keyword);
    }

    /**
     * Remove a keyword from the list of keywords for this card.
     * @return keyword a keyword that is to be removed from this card.
     */
    public void removeKeyword(Keyword keyword) {
        int id = keyword.getId();
        for (int i = 0; i < keywords.size(); i++){
            if (keywords.get(i).getId() == id){
                this.keywords.remove(i);
            }
        }
    }

    /**
     * Override the toString method for debugging purposes.
     * @return a string representing the marketplace card.
     */
    @Override
    public String toString() {
        return "{\"id\":" + id +
                ",\"creatorId\":\"" + creatorId + "\"" +
                ",\"creator\":\"" + creator + "\"" +
                ",\"section\":\"" + section + "\"" +
                ",\"created\":\"" + created + "\"" +
                ",\"title\":\"" + title + "\"" +
                ",\"description\":\"" + description + "\"" +
                //TODO add keyword stuff
                "}";
    }


    /**
     * Convert a Marketplace object into a MarketplacePayload
     * @return a MarketplacePayload object
     */
    public MarketplaceCardPayload toMarketplaceCardPayload() throws Exception {
        return new MarketplaceCardPayload(
                id,
                creator.toUserPayloadSecure(),
                section,
                created,
                displayPeriodEnd,
                title,
                description,
                toKeywordPayloads(keywords)
        );
    }

    /**
     * Converts the keywords belonging to a card to Keyword Payloads (needed by GET requests).
     * @param keywords the list of keywords belonging to the card.
     * @return a list of keywords converted to keyword payloads.
     */
    public List<KeywordPayload> toKeywordPayloads(List<Keyword> keywords) {
        List<KeywordPayload> keywordPayloads = new ArrayList<>();
        for (Keyword keyword: keywords) {
            KeywordPayload keywordPayload = new KeywordPayload(keyword.getId(), keyword.getName(), keyword.getCreated());
            keywordPayloads.add(keywordPayload);
        }
        return keywordPayloads;
    }

}