/**
 * Summary. This file contains the definition for the ListingRepository.
 *
 * Description. This file contains the defintion for the ListingRepository.
 *
 * @link   team-400/src/main/java/org/seng302/business/listing/ListingRepository
 * @file   This file contains the definition for ListingRepository.
 * @author team-400.
 * @since  5.5.2021
 */
package org.seng302.business.listing;

import org.seng302.business.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * ListingRepository interface
 */
@RepositoryRestResource
public interface ListingRepository extends JpaRepository<Listing, Integer> {

    /**
     * Finds any listings with the given business ID.
     *
     * @param businessId A business ID.
     * @return A list of products with the given business ID.
     */
     Page<Listing> findListingsByBusinessId(Integer businessId, Pageable paging);
}
