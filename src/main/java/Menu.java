import connect.HibenrnateUtil;
import dao.CityDAO;
import entity.City;
import org.hibernate.Session;
import service.MenuService;

import java.util.Scanner;

public class Menu {
    private static final int EXIT = 0;
    private static final int ALL_CITY = 1;
    private static final int CITY_ID = 2;
    private static final int DELETE_ID = 3;
    private static final String MENU_SELECT = """
            1. All_City
            2. City_Id               
            3. Delete_Id
            0. Exit
            """;
    private static boolean isRun = true;

    static final MenuService menuService = new MenuService();
    static final HibenrnateUtil CONNECTION_DATABASE = new HibenrnateUtil();
    static final Session currentSession = CONNECTION_DATABASE.getCurrentSession();
    static final CityDAO cityDAO = new CityDAO(City.class, currentSession.getSessionFactory());

    Scanner scanner = new Scanner(System.in);

    public void run() {
        while (isRun) {
            System.out.println(MENU_SELECT);
            int number = scanner.nextInt();
            if (number == ALL_CITY) {
                System.out.println(ALL_CITY);
                menuService.cityDataAll();
            } else if (number == CITY_ID) {
                System.out.println(CITY_ID);
                System.out.println("Enter the database ID");
                int numberCityId = scanner.nextInt();
                cityDAO.findById(numberCityId);
            } else if (number == DELETE_ID){
                System.out.println(DELETE_ID);
                System.out.println("Enter the database delete ID ");
                int numberCityId = scanner.nextInt();
                cityDAO.deleteById(numberCityId);
            } else  if (number == EXIT) {
                menuService.shutdown();
                isRun = false;
                System.out.println("By!");
            }
        }
    }
}