package view;

public class Admin extends User {
    private static Admin instance;
    
    private Admin() {
        super("admin");
    }
    
    public static Admin getInstance() {
        if(instance == null) {
            instance = new Admin();
        }
        return instance;
    }
}
