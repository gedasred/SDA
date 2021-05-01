import java.util.ArrayList;
import java.util.Random;

public class Bank {

    private final String name;

    private ArrayList<User> users;

    private ArrayList<Account> accounts;

    /**
     * Create a new bank object with empty lists of users and accounts
     * @param name  the name of the bank
     */
    public Bank(String name) {
        this.name = name;
        this.users = new ArrayList<>();
        this.accounts = new ArrayList<>();
    }

    /**
     * Generate a new universally unique ID for a user.
     *
     * @return generated UUID.
     */
    public String getNewUserUUID() {
        return generateNewUUID(6, this.users);
    }


    /**
     * Generate a new universally unique ID for an account.
     *
     * @return generated UUID
     */
    public String getNewAccountUUID() {
        return generateNewUUID(10, this.accounts);
    }


    private <T extends AbstractUUID> String generateNewUUID(int len, ArrayList<T> obj) {

        String uuid;
        Random rng = new Random();
        boolean nonUnique;

        // continue looping until we get a unique ID
        do {
            uuid = "";
            for (int c = 0; c < len; c++) {
                uuid += ((Integer) rng.nextInt(10)).toString();
            }

            // check to make sure it is unique
            nonUnique = false;
            for (T element : obj) {
                if (uuid.compareTo(element.getUUID()) == 0) {
                    nonUnique = true;
                    break;
                }
            }

        } while (nonUnique);

        return uuid;
    }

    public void addAccount(Account account) {
        this.accounts.add(account);
    }

    public User addUser(String firstName, String lastName, String pin) {

        // create a new User object and add it to our list
        User newUser = new User(firstName, lastName, pin, this);
        this.users.add(newUser);

        // create a savings accounts for the user

        Account newAccount = new Account("Savings account", newUser, this);
        // add to holder and bank lists
        newUser.addAccount(newAccount);
        this.accounts.add(newAccount);

        return newUser;
    }

    /**
     *
     * @param userID    the UUID of the user to log in
     * @param pin       the pin of the user
     * @return          the User obj, if login successful, on null, it it is not.
     */
    public User userLogin(String userID, String pin) {

        // search through list of users
        for (User u : this.users) {
            if (u.getUUID().compareTo(userID) == 0 && u.validatePin(pin)) {
                return u;
            }
        }

        // if user not found or incorrect pin
        return null;
    }

    public String getName() {
        return this.name;
    }
}