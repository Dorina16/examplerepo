package nopcommerce.tests;

import base.CommonAPI;
import nopcommerce.pages.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;
import utility.GenerateData;
import utility.ReadFromExcel;

public class checkoutTest extends CommonAPI {
    Logger log = LogManager.getLogger(checkoutTest.class.getName());
    ReadFromExcel readTitleFromExcel = new ReadFromExcel(System.getProperty("user.dir")+"\\Data\\nopcommerce\\nopCommerceData.xlsx","titles");
    ReadFromExcel readTestDataFromExcel = new ReadFromExcel(System.getProperty("user.dir")+"\\Data\\nopcommerce\\nopCommerceData.xlsx","CheckoutTestData");
    String itemName =readTestDataFromExcel.getCellValueForGivenHeaderAndKey("variable","itemName");
    String country = readTestDataFromExcel.getCellValueForGivenHeaderAndKey("variable","country");
    String validEmail = GenerateData.email();
    String validPassword = GenerateData.password();
    RegistrationPage registrationPage;
    CheckoutPage checkoutPage;
    HomePage homePage;
    SearchPage searchPage;
    ShoppingCartPage shoppingCartPage;
    LogInPageNop logInPageNop;

    //Demo website requires to create an account before testing.
    @Test
    public void createAccount () {
        homePage = new HomePage(getDriver());
        homePage.clkOnLnkRegister();
        String actualTitle = getCurrentTitle();
        String expectedRegisterPageTitle = readTitleFromExcel.getCellValueForGivenHeaderAndKey("title","register page");
        Assert.assertEquals(actualTitle, expectedRegisterPageTitle, "Did not land on registration page");
        log.info("Landed on registration page successfully");

        //Entering Credentials
        registrationPage = new RegistrationPage(getDriver());
        String firstName = GenerateData.firstName();
        registrationPage.setFirstName(firstName);
        String lastName = GenerateData.lastName();
        registrationPage.setLastName(lastName);
        registrationPage.setEmail(validEmail);
        registrationPage.setPassword(validPassword);
        registrationPage.setConfirmPassword(validPassword);

        registrationPage.clkOnRegister();

        String returnedMessage = registrationPage.readReturnedMessage();
        String msgRegistrationSuccess = readTestDataFromExcel.getCellValueForGivenHeaderAndKey("variable","msgRegistrationSuccess");
        Assert.assertEquals(returnedMessage, msgRegistrationSuccess);
        log.info("Registration success message returned");
    }


    //log in, add an item to cart, and click on checkout, confirm landing on checkout page
    @Test(priority = 0, dependsOnMethods = {"createAccount"})
    public void CheckoutWithoutAgreeingToTermsOfServiceTest() {
        log.info("***  Checkout Test CheckoutWithoutAgreeingToTermsOfServiceTest Started ***");
        homePage = new HomePage(getDriver());
        homePage.clkOnLinkLogin();
        log.info("Landed on login page success");
        logInPageNop = new LogInPageNop(getDriver());
        logInPageNop.logIn(validEmail,validPassword);

        homePage.typeItemAndClickSearch(itemName);
        log.info("Landed on search page successfully");
        //Validate Item Name
        searchPage = new SearchPage(getDriver());
        String actualSearchItemName = searchPage.getItemName();
        Assert.assertEquals(actualSearchItemName, itemName,"Item not found");
        log.info("Item found successfully");
        //Add to cart
        searchPage.addToCart();
        //go to cart
        homePage.clkOnLinkShoppingCart();
        shoppingCartPage = new ShoppingCartPage(getDriver());
        //get product name in cart
        String actualShoppingCartProductName = shoppingCartPage.getCartProductName();
        Assert.assertEquals(actualShoppingCartProductName,itemName);
        log.info("Item added to Shopping cart Successfully");

        shoppingCartPage.clickOnCheckout();
        String actualTermsOfServiceMsg = shoppingCartPage.getTextTermsOfServiceBox();
        String expectedTermsOfServiceMsg = readTestDataFromExcel.getCellValueForGivenHeaderAndKey("variable","expectedTermsOfServiceMsg");
        Assert.assertEquals(actualTermsOfServiceMsg, expectedTermsOfServiceMsg);
        String actualTitle = getCurrentTitle();

        String expectedShoppingCartTitle = readTitleFromExcel.getCellValueForGivenHeaderAndKey("title","shopping cart page");
        Assert.assertEquals(actualTitle, expectedShoppingCartTitle);
        log.info("Stayed onShopping Cart page successfully");

        log.info("***  Checkout Test CheckoutWithoutAgreeingToTermsOfServiceTest Ended ***");
    }


    //log in,  go to cart, click on checkout and fill checkout form, submit order
    @Test(priority = 1, dependsOnMethods = {"createAccount"})
    public void fillingCheckoutFromTest() {
        log.info("***  Checkout Test fillingCheckoutFromTest Started ***");
        homePage = new HomePage(getDriver());
        homePage.clkOnLinkLogin();
        log.info("Landed on login page success");

        logInPageNop = new LogInPageNop(getDriver());
        logInPageNop.logIn(validEmail,validPassword);

        //go to cart
        homePage.clkOnLinkShoppingCart();
        shoppingCartPage = new ShoppingCartPage(getDriver());
        //get product name in cart
        String actualShoppingCartProductName = shoppingCartPage.getCartProductName();
        Assert.assertEquals(actualShoppingCartProductName,itemName);
        log.info("Item found in Shopping cart Successfully");

        shoppingCartPage.checkAgreeToTermsOfService();
        shoppingCartPage.clickOnCheckout();

        checkoutPage = new CheckoutPage(getDriver());
        checkoutPage.SelectCountry(country);
        String city = GenerateData.city();
        checkoutPage.setCityName(city);
        String address = GenerateData.address();
        checkoutPage.setAddress1(address);
        String zipCode = GenerateData.zipCode();
        checkoutPage.setZipCode(zipCode);
        String phoneNumber = GenerateData.phoneNumber();
        checkoutPage.setPhoneNumber(phoneNumber);
        checkoutPage.clickContinueButtonInBillingAddress();
        checkoutPage.clickContinueShippingMethod();
        checkoutPage.clickContinueInPaymentMethod();
        checkoutPage.clickContinueInPaymentInformation();
        checkoutPage.clickConfirm();

        String actualConfirmationMessage = checkoutPage.getConfirmationSuccess();
        String expectedConfirmationMessage = readTestDataFromExcel.getCellValueForGivenHeaderAndKey("variable","expectedConfirmationMessage");
        Assert.assertEquals(actualConfirmationMessage,expectedConfirmationMessage);
        log.info("Order has been placed successfully");

        log.info("***  Checkout Test fillingCheckoutFromTest Ended ***");
    }
}
