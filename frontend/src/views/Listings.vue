<template>
  <div>
    <div id="main">
    <!-- Navbar -->
    <Navbar/>
    <!-- Listing Creation -->
    <create-listing @updateListings="afterCreation"
                    v-bind:currency-code="currencyCode"
                    v-bind:currency-symbol="currencySymbol"/>
    <!-- Listing Container -->
    <div class="container">
      <h1 id="pageTitle">{{ businessName }}'s Listings</h1>
      <div class="card p-1">
        <!-- Order Buttons -->
        <div class="row my-3" align="center">
          <!--filter-->
          <div class="btn-group col-3 py-1" role="group">
            <button type="button" class="btn green-button dropdown-toggle col-4"
                    data-bs-toggle="dropdown" aria-expanded="false">Filter Option
            </button>

            <ul class="dropdown-menu gap-2" aria-labelledby="btnGroupDrop1">
              <!--order by quantity-->
              <button type="button" class="btn green-button-transparent col-12"
                      @click="orderListings(true, false, false, false)">
                Quantity
                <i id="quantityIcon"></i>
              </button>

              <!--order by price-->
              <button type="button" class="btn green-button-transparent col-12"
                      @click="orderListings(false, true, false, false)">
                Price
                <i id="priceIcon"></i>
              </button>

              <!--order by closing date-->
              <button type="button" class="btn green-button-transparent col-12"
                      @click="orderListings(false, false, true, false)">
                Closing Date
                <i id="closesIcon"></i>
              </button>

              <!--order by listing date-->
              <button type="button" class="btn green-button-transparent col-12"
                      @click="orderListings(false, false, false, true)">
                Listing Date
                <i id="createdIcon"></i>
              </button>
            </ul>
          </div>

          <!-- Add new Button -->
          <div class="col-md" v-if="businessAdmin">
            <button type="button" class="btn green-button w-75 my-1" data-bs-toggle="modal" data-bs-target="#listingCreationPopup">Add new</button>
          </div>

          <div class="col-12 col-md-6 text-secondary px-3 flex-nowrap">Filter By: {{convertToString()}}</div>

        </div>

        <!--space-->
        <br>

        <!--creation success info-->
        <div class="alert alert-success" role="alert" v-if="creationSuccess">
          <div class="row">
            <div class="col" align="center">New Listing Created</div>
          </div>
        </div>

        <!-- Listings -->
        <ListingItem
            v-for="item in listings"
            v-bind:key="item.index"
            v-bind:product-name="item.productName"
            v-bind:description="item.description"
            v-bind:product-id="item.productId"
            v-bind:quantity="item.quantity"
            v-bind:price="item.price"
            v-bind:listDate="item.listDate"
            v-bind:close-date="item.closeDate"
            v-bind:best-before="item.bestBefore"
            v-bind:expires="item.expires"
            v-bind:moreInfo="item.moreInfo"
            v-bind:currency-code="currencyCode"
            v-bind:currency-symbol="currencySymbol"
        />

        <!--space-->
        <br>

        <!---------------------------------------------- page buttons ------------------------------------------------>

        <div id="page-button-container">
          <PageButtons
              v-bind:totalPages="totalPages"
              v-bind:currentPage="currentPage"
              @updatePage="updatePage"/>
        </div>

      </div>
    </div>
    <div class="card p-1" v-if="listings.length < 1">
      <p class="h2 py-5" align="center">No Listings Found</p>
    </div>
    </div>
    <!-- Footer -->
    <Footer class="footer"/>
  </div>
</template>

<script>
import Navbar from "@/components/main/Navbar";
import ListingItem from "@/components/listing/ListingItem";
import Api from "@/Api";
import Cookies from "js-cookie";
import CreateListing from "@/components/listing/CreateListingModal";
import Footer from "@/components/main/Footer";
import CurrencyAPI from "@/currencyInstance";
import PageButtons from "../components/PageButtons";
import {formatDate} from "../dateUtils";


export default {
name: "Listings",
  components: {Footer, CreateListing, ListingItem, Navbar, PageButtons},
  data() {
    return {
      allListings: [],
      listings: [],
      businessName: "",
      businessAdmin: false,
      businessId: -1,
      role: "",
      currentUser: -1,

      orderBy: "",
      rowsPerPage: 5,
      currentPage: 0,
      totalPages: 0,
      totalRows: 0,

      quantityAscending: false,
      priceAscending: false,
      closesAscending: false,
      createdAscending: false,

      currencyCode: "",
      currencySymbol: "",

      creationSuccess: false
    }
  },
  methods: {
    /**
     * convert orderByString to more readable for user
     */
    convertToString() {
      switch (this.orderBy) {
        case 'quantityASC': return "Quantity Ascending";
        case 'quantityDESC': return "Quantity Descending";
        case 'priceASC': return "Price Ascending";
        case 'priceDESC': return "Price Descending";
        case 'closesASC': return "Closes Ascending";
        case 'closesDESC': return "Closes Descending";
        case 'createdASC': return "Created Ascending";
        case 'createdDESC': return "Created Descending";
      }
    },
    /**
     * Updates the display to show the new page when a user clicks to move to a different page.
     *
     * @param newPageNumber The new page number
     */
    updatePage(newPageNumber) {
      this.currentPage = newPageNumber;
      this.$router.push({path: `/businessProfile/${this.businessId}/listings`, query: {"orderBy": this.orderBy, "page": (this.currentPage + 1).toString()}})
      this.getListings();
    },

    /**
     * Given a page number check that the page is within the acceptable range.
     * NOTE this is a 0 origin.
     * @param pageNumber The page number to be checked.
     */
    isValidPageNumber(pageNumber) {
      return 0 <= pageNumber && pageNumber < this.totalPages;
    },

    /**
     * Orders the listings based on the given booleans for each column, and updates the display
     * @param quantity Boolean, whether to order by quantity
     * @param price Boolean, whether to order by price
     * @param closes Boolean, whether to order by closing date
     * @param created Boolean, whether to order by listing date
     */
    orderListings(quantity, price, closes, created) {

      if (quantity) {
        this.disableIcons();
        if (this.quantityAscending) {
          this.orderBy = "quantityASC"
          document.getElementById('quantityIcon').setAttribute('class', 'fas fa-chevron-up float-end');
        } else {
          this.orderBy = "quantityDESC"
          document.getElementById('quantityIcon').setAttribute('class', 'fas fa-chevron-down float-end');
        }

        this.quantityAscending = !this.quantityAscending;
        this.priceAscending = false;
        this.closesAscending = false;
        this.createdAscending = false;

      } else if (price) {
        this.disableIcons();
        if (this.priceAscending) {
          this.orderBy = "priceASC"
          document.getElementById('priceIcon').setAttribute('class', 'fas fa-chevron-up float-end');
        } else {
          this.orderBy = "priceDESC"
          document.getElementById('priceIcon').setAttribute('class', 'fas fa-chevron-down float-end');
        }

        this.quantityAscending = false;
        this.priceAscending = !this.priceAscending;
        this.closesAscending = false;
        this.createdAscending = false;

      } else if (closes) {
        this.disableIcons();
        if (this.closesAscending) {
          this.orderBy = "closesASC"
          document.getElementById('closesIcon').setAttribute('class', 'fas fa-chevron-up float-end');
        } else {
          this.orderBy = "closesDESC"
          document.getElementById('closesIcon').setAttribute('class', 'fas fa-chevron-down float-end');
        }

        this.quantityAscending = false;
        this.priceAscending = false;
        this.closesAscending = !this.closesAscending;
        this.createdAscending = false;

      } else if (created) {
        this.disableIcons();
        if (this.createdAscending) {
          this.orderBy = "createdASC"
          document.getElementById('createdIcon').setAttribute('class', 'fas fa-chevron-up float-end');
        } else {
          this.orderBy = "createdDESC"
          document.getElementById('createdIcon').setAttribute('class', 'fas fa-chevron-down float-end');
        }

        this.quantityAscending = false;
        this.priceAscending = false;
        this.closesAscending = false;
        this.createdAscending = !this.createdAscending;

      }

      this.$router.push({path: `/businessProfile/${this.businessId}/listings`, query: {"orderBy": this.orderBy, "page": (this.currentPage + 1).toString()}});
      this.getListings();
    },

    /**
     * Disables all ascending or descending icons in the filter buttons.
     */
    disableIcons() {
      document.getElementById('quantityIcon').setAttribute('class', '');
      document.getElementById('priceIcon').setAttribute('class', '');
      document.getElementById('closesIcon').setAttribute('class', '');
      document.getElementById('createdIcon').setAttribute('class', '');
    },

    async getListings() {
      /*
      Attempts to get listings from backend
      If successful, sends data to populatePage()
      If not, redirects to appropriate page
      */
      this.orderBy = this.$route.query["orderBy"] || "closesASC";
      this.currentPage = parseInt(this.$route.query["page"]) - 1 || 0;

      await Api.sortListings(this.businessId, this.orderBy, this.currentPage).then(response => {
        this.totalRows = parseInt(response.headers["total-rows"]);
        this.totalPages = parseInt(response.headers["total-pages"]);

        if (this.totalPages > 0 && this.currentPage > this.totalPages - 1) {
          this.$router.push({path: '/pageDoesNotExist'});
        }

        this.populatePage(response);

      }).catch((error) => {
        if (error.request && !error.response) {
          this.$router.push({path: '/timeout'});
        } else if (error.response.status === 400) {
          this.$router.push({path: '/pageDoesNotExist'});
        } else if (error.response.status === 401) {
          this.$router.push({path: '/invalidtoken'});
        } else if (error.response.status === 406) {
          this.$router.push({path: '/noBusiness'});
        } else {
          this.$router.push({path: '/timeout'});
          console.log(error.message);
        }
      })
    },
    getBusiness(id) {
      Api.getBusiness(id).then(response => (this.getBusinessData(response.data))).catch((error) => {
        if (error.request && !error.response) {
          this.$router.push({path: '/timeout'});
        } else if (error.response.status === 401) {
          this.$router.push({path: '/invalidtoken'});
        } else if (error.response.status === 406) {
          this.$router.push({path: '/noBusiness'});
        } else {
          this.$router.push({path: '/noBusiness'});
          console.log(error.message);
        }
      })
    },
    getBusinessData(data) {
      this.businessName = data.name;
      // Checks if user is acting as business
      const actAs = Cookies.get('actAs');
      this.businessAdmin = actAs === String(data.id);
      // Checks if user is a global admin
      if (actAs === undefined && this.businessAdmin === false) {
        data.administrators.forEach(user => {
          if (this.currentUser === user.id.toString()) {
            this.businessAdmin = true;
          }
        });
        if (this.role === "DEFAULTGLOBALAPPLICATIONADMIN" || this.role === "GLOBALAPPLICATIONADMIN") {
          this.businessAdmin = true;
        }
      }
    },
    populatePage(response) {
      if (response.data.length <= 0) {
        this.currentPage = 0;
        this.maxPage = 0;
        this.totalRows = 0;
        this.totalPages = 0;
        // Generate the tableData to be placed in the table & get the total number of rows.
      } else {
        this.listings = [];

        for (let i = 0; i < this.rowsPerPage; i++) {
          if (i === response.data.length) {
            return
          }
          this.listings.push({
            productName: response.data[i].inventoryItem.product.name,
            description: response.data[i].inventoryItem.product.description,
            productId: response.data[i].inventoryItem.product.id,
            quantity: response.data[i].quantity,
            price: response.data[i].price,
            listDate: formatDate(response.data[i].created, false),
            closeDate: formatDate(response.data[i].closes, false),
            moreInfo: response.data[i].moreInfo,
            expires: formatDate(response.data[i].inventoryItem.expires, false)
          })
        }
      }
    },

    /**
     * Currency API requests.
     * An asynchronous function that calls the REST Countries API with the given country input.
     * Upon success, the filterResponse function is called with the response data.
     */
    async currencyRequest() {
      /*
        Request business from backend. If received assign the country of the business
        to a variable.
        */
      let country = "";
      await Api.getBusiness(this.businessId).then((response) => {
        country = response.data.address.country;
      })
          .catch((error) => console.log(error))

      await CurrencyAPI.currencyQuery(country).then((response) => {
        this.filterResponse(response.data);
      })
          .catch((error) => console.log(error))
    },

    /**
     * Retrieves the currency code and symbol that we want from the API response.
     * @param response The response from the REST countries API
     */
    filterResponse(response) {
      this.currencyCode = response[0].currencies[0].code;
      this.currencySymbol = response[0].currencies[0].symbol;
    },

    async getUserRole(id) {
      await Api.getUser(id).then(response => {
        this.role = response.data.role;
      })
    },
    /**
     * After creation success use endpoint to collect data from backend and display it.
     */
    afterCreation() {
      this.creationSuccess = true;
      // The corresponding alert will close automatically after 5000ms.
      setTimeout(() => {
        this.creationSuccess = false
      }, 5000);
      this.getListings();
    },
  },
  async mounted() {
    /**
     * When mounted, initiate population of page.
     * If cookies are invalid or not present, redirect to login page.
     */
    this.currentUser = Cookies.get('userID');
    if (this.currentUser) {
      await this.getUserRole(this.currentUser);
      this.businessId = await parseInt(this.$route.params.id);
      await this.getBusiness(this.businessId);

      await this.currencyRequest();

      this.getListings().catch(
          (e) => console.log(e)
      )
    }
  }
}
</script>

<style scoped>
#pageTitle {
  padding: 10px;
  text-align: center;
}
</style>
