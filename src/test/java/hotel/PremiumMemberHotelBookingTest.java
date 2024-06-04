package hotel;

import hotel.pages.ReservePage;
import hotel.pages.TopPage;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static hotel.Utils.BASE_URL;
import static hotel.Utils.getNewWindowHandle;
import static hotel.Utils.sleep;
import static org.junit.jupiter.api.Assertions.assertEquals;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("プレミアム会員でのホテル予約")
public class PremiumMemberHotelBookingTest {

    private static WebDriver driver;
    private static WebDriverWait wait;
    private String originalHandle;


    @BeforeAll
    static void initAll() {
        driver = Utils.createWebDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @BeforeEach
    void init() {
        originalHandle = driver.getWindowHandle();
    }

    @AfterEach
    void tearDown() {
        if (driver.getWindowHandles().size() > 1) {
            driver.close();
        }
        driver.switchTo().window(originalHandle);
        driver.manage().deleteAllCookies();
    }

    @AfterAll
    static void tearDownAll() {
        driver.quit();
    }

    @Test
    @Order(1)
    @DisplayName("プレミアム会員でログインができること")
    void testScenarioPremiumMemberLoginSuccess() {
        driver.get(BASE_URL);
        var topPage = new TopPage(driver);
        var loginPage = topPage.goToLoginPage();
        var myPage = loginPage.doLogin("ichiro@example.com", "password");
        var originalHandles = driver.getWindowHandles();

        var plansPage = myPage.goToPlansPage();
        plansPage.openPlanByTitle("テーマパーク優待プラン");
        sleep(500);

        var newHandles = driver.getWindowHandles();
        var newHandle = getNewWindowHandle(originalHandles, newHandles);
        driver.switchTo().window(newHandle);
        var reservePage = new ReservePage(driver);
        reservePage.setReserveDate("2024/07/15");
        reservePage.setReserveTerm("3");
        reservePage.setHeadCount("2");
        reservePage.setBreakfastPlan(true);
        reservePage.setContact(ReservePage.Contact.電話でのご連絡);
        reservePage.setTel("00011112222");

        var confirmPage = reservePage.goToConfirmPage();

        assertEquals("合計 66,000円（税込み）", confirmPage.getTotalBill());

        confirmPage.doConfirm();
        assertEquals("予約を完了しました", confirmPage.getModalTitle());

        confirmPage.close();
    }
}
