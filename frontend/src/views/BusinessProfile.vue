<template>
  <div>

    <div id="main">
    <!--nav bar-->
    <Navbar></Navbar>

    <!--profile header, contains user search bar-->
    <div id="profile-header-div">
      <ProfileHeader/>
    </div>

    <!--profile container-->
    <div class="container p-5 mt-3" id="profileContainer">
      <div class="row">
        <div class="col-xl-3 mb-3">

          <div class="card text-center shadow-sm">
            <div class="card-body">

              <!--business's profile image-->
              <img class="rounded-circle img-fluid" :src="require('../../public/sample_business_logo.jpg')"
                   alt="Profile Image"/>

              <!--business's name-->
              <div class="mt-3">
                <h5>{{ name }}</h5>
                <div class="text-secondary">{{ description }}</div>
              </div>
            </div>

            <!--       for later use     -->
            <!--            <div class="card text-center shadow-sm-3">-->
            <!--              <div class="card-body">-->
            <!--                <button class="btn btn-lg text-secondary" id="editProfileButton">Edit Profile</button>-->
            <!--              </div>-->
            <!--            </div>-->
          </div>

        </div>

        <div class="col">
          <div class="card shadow-sm">
            <div class="card-body">

              <!--business's name-->
              <div class="container">
                <div class="row justify-content-between">
                  <div class="col-4 -align-left">
                    <h6>Name:</h6>
                  </div>
                  <div class="col-8">
                    <div class="text-secondary" align="right">{{ name }}</div>
                  </div>
                </div>
              </div>

              <!--business's type-->
              <hr>
              <div class="container">
                <div class="row justify-content-between">
                  <div class="col-4 -align-left">
                    <h6>Business Type:</h6>
                  </div>
                  <div class="col-8">
                    <div class="text-secondary" align="right">{{ businessType }}</div>
                  </div>
                </div>
              </div>

              <!--business's created time-->
              <hr>
              <div class="container">
                <div class="row justify-content-between">
                  <div class="col-4 -align-left">
                    <h6>Created Time:</h6>
                  </div>
                  <div class="col-8">
                    <div class="text-secondary" align="right">{{ created }}</div>
                  </div>
                </div>
              </div>

              <!--business's address-->
              <hr>
              <div class="container">
                <div class="row justify-content-between">
                  <div class="col-4 -align-left">
                    <h6>Address:</h6>
                  </div>
                  <div class="col-8">
                    <div class="row">
                      <div class="text-secondary" v-for="lines in address" :key="lines.line" align="right">
                        {{ lines.line }}
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <!--business's primary administrator-->
              <hr v-if="isAdministrator">
              <div class="container" v-if="isAdministrator">
                <div class="row justify-content-between">
                  <div class="col-4 -align-left">
                    <h6>Primary Administrator:</h6>
                  </div>
                  <div class="col-8">
                    <div class="text-secondary primary-administrator" align="right" @click="pushToUser(primaryAdministratorId)">
                      {{ primaryAdministrator }}
                    </div>
                  </div>
                </div>
              </div>

              <!--business's administrators-->
              <hr>
              <div class="container">
                <div class="row justify-content-between">
                  <div class="col-4 -align-left">
                    <h6>Administrators:</h6>
                  </div>
                  <div class="col-8">
                    <div class="text-secondary other-administrators" v-for="nameOfAdministrator in nameOfAdministrators"
                         :key="nameOfAdministrator.name"
                         align="right" @click="pushToUser(nameOfAdministrator.id)">
                      {{ nameOfAdministrator.name }}
                    </div>
                  </div>
                </div>
              </div>

            </div>
          </div>

          <div class="row">
            <div class="col">
              <button class="btn green-button mt-4" @click="navigateTo('Listings')" tabindex="11">Listings</button>
            </div>
            <div class="col">
              <!--logout button-->
              <div align="right" id="adminButtonRow" v-if="isAdministrator">
                <button class="btn green-button float-end mt-4 mx-2" id="productCatalogueButton"
                        @click="navigateTo('ProductCatalogue')" tabindex="13">Product Catalogue

                </button>
                <button class="btn green-button float-end mt-4 mx-2" id="InventoryButton"
                        @click="navigateTo('Inventory')" tabindex="12">Inventory
                </button>

              </div>
            </div>
          </div>




        </div>
      </div>
    </div>

    </div>
    <!--footer-->
    <Footer/>

  </div>
</template>

<script>
import ProfileHeader from "@/components/ProfileHeader";
import Footer from "@/components/main/Footer";
import Navbar from "@/components/main/Navbar";
import Api from "@/Api";
import Cookies from 'js-cookie';
import {UserRole} from "@/configs/User";

export default {
  name: "BusinessProfile",
  components: {
    Footer,
    ProfileHeader,
    Navbar
  },
  data() {
    return {
      urlId: null,
      name: "",
      description: "",
      businessType: "",
      created: "",
      primaryAdministrator: "",
      primaryAdministratorId: "",

      address: [],
      streetNumber: "",
      streetName: "",
      city: "",
      region: "",
      country: "",
      postcode: "",

      nameOfAdministrators: [],

      isAdministrator: false
    }
  },
  methods: {
    formatAge(ageString) {
      /*
      Formats the given age string using a Date object and removes the day from the result.
      Returns a formatted string.
       */
      let array = (new Date(ageString)).toDateString().split(" ");
      array.shift();
      return array.join(' ')
    },
    getCreatedDate(createdDate) {
      /*
      Calculates the months between the given date and the current date, then formats the given date and months.
      Finally it sets the join date on the page to the formatted string.
       */

      const dateJoined = new Date(createdDate);

      const currentDate = new Date();
      let months = (currentDate.getFullYear() - dateJoined.getFullYear()) * 12
          + (currentDate.getMonth() - dateJoined.getMonth());

      // getDate instead of getDay is important
      // "The value returned by getDay
      // is an integer corresponding to the day of the week:
      // 0 for Sunday, 1 for Monday, 2 for Tuesday, and so on."
      if (currentDate.getDate() < dateJoined.getDate()) {
        months -= 1;
      }

      const finalDate = this.formatAge(createdDate);
      this.created = `${finalDate} (${months} months ago)`;
    },
    retrieveBusiness(businessID) {
      /*
      try to send a request to backend, and use populatePage() function to unpack data returned,
      if fail, push to a page show the error.
      */
      Api.getBusiness(businessID).then(response => (this.populatePage(response.data))).catch((error) => {
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
    populatePage(data) {
      /*
      unpack data
      */

      //basic data unpack
      this.name = data.name;
      this.description = data.description;
      let businessTypeLowerCaseAndSplit = data.businessType.replaceAll("_", " ").toLowerCase().split(" ");
      for (let i = 0; i < businessTypeLowerCaseAndSplit.length; i++) {
        businessTypeLowerCaseAndSplit[i] = businessTypeLowerCaseAndSplit[i][0].toUpperCase() + businessTypeLowerCaseAndSplit[i].slice(1);
      }
      this.businessType = businessTypeLowerCaseAndSplit.join(" ");
      this.getCreatedDate(data.created);

      // address unpack
      //address unpack
      if (data.address.streetNumber) {
        this.streetNumber = data.address.streetNumber;
      }
      if (data.address.streetName) {
        this.streetName = data.address.streetName;
      }
      if (data.address.suburb) {
        this.suburb = data.address.suburb;
      }
      if (data.address.city) {
        this.city = data.address.city;
      }
      if (data.address.region) {
        this.region = data.address.region;
      }
      if (data.address.country) {
        this.country = data.address.country;
      }
      if (data.address.postcode) {
        this.postcode = data.address.postcode;
      }

      if (this.streetNumber !== "" && this.streetName !== "") {
        this.address.push({line: this.streetNumber + " " + this.streetName});
      } else {
        this.address.push({line: this.streetNumber + this.streetName});
      }
      if (this.suburb !== "") {
        this.address.push({line: this.suburb});
      }
      if (this.city !== "" && this.postcode !== "") {
        this.address.push({line: this.city + ", " + this.postcode});
      } else {
        this.address.push({line: this.city + this.postcode});
      }
      if (this.region !== "" && this.country !== "") {
        this.address.push({line: this.region + ", " + this.country});
      } else {
        this.address.push({line: this.region + this.country});
      }

      // administrators unpack
      this.primaryAdministratorId = data.primaryAdministratorId;
      data.administrators.forEach(anUser => {

        // This is in case administrator doesn't have a middle name.
        let adminMiddleName = "";
        if (anUser.middleName) {
          adminMiddleName = anUser.middleName;
        }

        //check permission of current user
        if (anUser.id === Cookies.get('userID') || this.$route.params.id === Cookies.get('actAs')) {
          this.isAdministrator = true;
        }

        //get name of primary administrator
        if (anUser.id === this.primaryAdministratorId) {
          this.primaryAdministrator = anUser.firstName + " " + adminMiddleName + " " + anUser.lastName;
        }
        this.nameOfAdministrators.push({
          name: anUser.firstName + " " + adminMiddleName + " " + anUser.lastName,
          id: anUser.id
        })
      })
    },
    pushToUser(id) {
      this.$router.push({name: 'Profile', params: {id}});
    },
    navigateTo(name) {
      /*
      Navigates to the product catalogue of the business
       */
      let id = this.$route.params.id;
      this.$router.push({name: name, params: {id}});
    },
    checkIsAdmin(currentID) {
      Api.getUser(currentID).then(response => {
        response.data.businessesAdministered.forEach(business => {
          if (business.id.toString() === this.$route.params.id) {
            this.isAdministrator = true;
          }
        });
        this.isAdministrator = this.isAdministrator ? true :
            (response.data.role === UserRole.DEFAULTGLOBALAPPLICATIONADMIN
            || response.data.role === UserRole.GLOBALAPPLICATIONADMIN);
        if (Cookies.get('actAs') !== undefined && this.$route.params.id !== Cookies.get('actAs')) {
          this.isAdministrator = false;
        }
      }).catch((error) => {
        if (error.request && !error.response) {
          this.$router.push({path: '/timeout'});
        } else if (error.response.status === 406) {
          this.$router.push({path: '/noUser'});
        } else if (error.response.status === 401) {
          this.$router.push({path: '/invalidtoken'});
        } else {
          this.$router.push({path: '/noUser'});
          console.log(error.message);
        }
      })
    },
  },
  mounted() {
    const currentID = Cookies.get('userID');
    if (currentID) {
      this.checkIsAdmin(currentID);
      const url = document.URL;
      const urlID = url.substring(url.lastIndexOf('/') + 1);

      if (urlID === 'businessProfile') {
        this.$router.push({path: '/noBusiness'});
      } else {
        this.retrieveBusiness(urlID);
        this.urlID = urlID;
      }
    }
  },
  beforeRouteUpdate (to, from, next) {
    // Reset variables
    this.name = "";
    this.description = "";
    this.businessType = "";
    this.created = "";
    this.primaryAdministrator = "";
    this.primaryAdministratorId = "";

    this.address = [];
    this.streetNumber = "";
    this.streetName = "";
    this.city = "";
    this.region = "";
    this.country = "";
    this.postcode = ""

    this.nameOfAdministrators = [];

    this.isAdministrator = false;

    const id = to.params.id;
    this.retrieveBusiness(id);
    next();
  },
}
</script>

<style scoped>

.primary-administrator:hover, .other-administrators:hover {
  color: #1EBA8C !important;
  cursor: pointer;
}

#profileContainer {
  margin-bottom: 5%;
}
</style>
