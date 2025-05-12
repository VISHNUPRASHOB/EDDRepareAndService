package eddrepareandservice;

import Controller.AdministratorController;
import Controller.CustomerController;
import Controller.TechnicianController;
import FileManager.RepairWorkCSVFileManager;
import FileManager.SpairPartsCSVFileManager;
import FileManager.TechnicianCSVFileManager;
import Helper.DataTransferHelper;
import Helper.LogHelper;
import Helper.NotificationHelper;
import Helper.OTPServiceHelper;
import Helper.TypeValidationHelper;
import Models.JobPart;
import Models.RepairWork;
import Models.SparePart;
import Models.Status;
import Models.Technician;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Collectors;

public class EDDRepareAndService {

    private static final Scanner scanner = new Scanner(System.in);
    private static final CustomerController customerController = new CustomerController();
    private static final AdministratorController adminController = new AdministratorController();
    private static final TechnicianController techController = new TechnicianController();
    private static final OTPServiceHelper otpHelper = new OTPServiceHelper();
    private static final RepairWorkCSVFileManager repairManager = new RepairWorkCSVFileManager();
    private static final TechnicianCSVFileManager techManager = new TechnicianCSVFileManager();
    private static final SpairPartsCSVFileManager sparePartsManager = new SpairPartsCSVFileManager();
    private static final NotificationHelper notificationHelper = new NotificationHelper();
    private static final LogHelper logHelper = new LogHelper();
    private static boolean isUserLoggedIn = false;
    private static String loggedInUserEmail = "";
    private static boolean isAdminLoggedIn = false;

    public static void main(String[] args) {
        showMainMenu();
    }

    private static void showMainMenu() {
        while (true) {
            System.out.println("\n------ EDD Technologies Private Limited ------");
            System.out.println("1. User");
            System.out.println("2. Administrator");
            System.out.println("3. Technician");
            System.out.println("0. Exit");
            System.out.print("Select role: ");

            int choice = getIntInput();
            switch (choice) {
                case 1:
                    userMenu();
                    break;
                case 2:
                    adminMenu();
                    break;
                case 3:
                    technicianMenu();
                    break;
                case 0:
                    System.out.println("Exiting system. Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void userMenu() {
        while (true) {
            if (!isUserLoggedIn) {
                System.out.println("\n------ User Menu ------");
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("0. Back to Main Menu");
                System.out.print("Select option: ");

                int choice = getIntInput();
                switch (choice) {
                    case 1:
                        userRegister();
                        break;
                    case 2:
                        userLogin();
                        break;
                    case 0:
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } else {
                System.out.println("\n------ Customer Dashboard ------");
                System.out.println("Logged in as: " + loggedInUserEmail);
                System.out.println("1. Book Repair");
                System.out.println("2. View Notifications");
                System.out.println("0. Logout");
                System.out.print("Select option: ");

                int choice = getIntInput();
                switch (choice) {
                    case 1:
                        bookRepair();
                        break;
                    case 2:
                        customerNotifications();
                        break;
                    case 0:
                        isUserLoggedIn = false;
                        loggedInUserEmail = "";
                        System.out.println("Logged out successfully.");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        }
    }

    private static void userRegister() {
        System.out.println("\n------ User Registration ------");
        System.out.print("Enter your name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter your email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Enter your phone number: ");
        String phone = scanner.nextLine().trim();

        StringBuilder errors = new StringBuilder();
        if (name.isEmpty()) {
            errors.append("• Name is required\n");
        }

        String emailError = TypeValidationHelper.emailValidator(email);
        if (emailError != null) {
            errors.append("• ").append(emailError).append("\n");
        }

        String phoneError = TypeValidationHelper.phoneNumberValidator(phone);
        if (phoneError != null) {
            errors.append("• ").append(phoneError).append("\n");
        }

        if (errors.length() > 0) {
            System.out.println("\nValidation Errors:\n" + errors);
        } else {
            DataTransferHelper<Boolean, String> result = customerController.userRegistration(name, email, phone, false);
            System.out.println("\n" + result.second);
        }
    }

    private static void userLogin() {
        System.out.println("\n------ User Login ------");
        System.out.print("Enter your email: ");
        String email = scanner.nextLine().trim();

        Boolean isExist = customerController.isEmailExistInCustomer(email);
        if (!isExist) {
            System.out.println("Email not found. Please register first.");
            return;
        }

        otpHelper.sendOtp(email);
        System.out.print("Enter the OTP sent to your email: ");
        int enteredOtp = getIntInput();

        if (enteredOtp == otpHelper.getOtp()) {
            isUserLoggedIn = true;
            loggedInUserEmail = email;
            System.out.println("Login successful as " + loggedInUserEmail);
        } else {
            System.out.println("Invalid OTP. Login failed.");
        }
    }

    private static void bookRepair() {
        System.out.println("\n------ Book Equipment Repair ------");
        System.out.print("Enter equipment name: ");
        String equipment = scanner.nextLine().trim();

        System.out.print("Enter issue description: ");
        String issue = scanner.nextLine().trim();

        if (equipment.isEmpty() || issue.isEmpty()) {
            System.out.println("All fields are required!");
            return;
        }

        String jobId = "JOB" + System.currentTimeMillis();
        RepairWork newJob = new RepairWork(jobId, loggedInUserEmail, null, equipment, issue, Status.JOB_CREATED);

        DataTransferHelper<Boolean, String> result = repairManager.saveRepairWork(newJob);
        System.out.println("\n" + result.second);
        if (result.first) {
            System.out.println("Your Job ID: " + jobId);
        }
    }

    private static void customerNotifications() {
        notificationHelper.viewNotification(loggedInUserEmail);
    }

    private static void adminMenu() {
        while (true) {
            if (!isAdminLoggedIn) {
                System.out.println("\n------ Admin Menu ------");
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("0. Back to Main Menu");
                System.out.print("Select option: ");

                int choice = getIntInput();
                switch (choice) {
                    case 1:
                        adminRegister();
                        break;
                    case 2:
                        adminLogin();
                        break;
                    case 0:
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } else {
                System.out.println("\n------ Admin Dashboard ------");
                System.out.println("1. Manage Customers");
                System.out.println("2. Manage Technicians");
                System.out.println("3. Manage Repair Jobs");
                System.out.println("4. Manage Spare Parts");
                System.out.println("5. Send Customer Notification");
                System.out.println("0. Logout");
                System.out.print("Select option: ");

                int choice = getIntInput();
                switch (choice) {
                    case 1:
                        manageCustomers();
                        break;
                    case 2:
                        manageTechnicians();
                        break;
                    case 3:
                        manageRepairJobs();
                        break;
                    case 4:
                        manageSpareParts();
                        break;
                    case 5:
                        sendCustomerNotification();
                        break;
                    case 0:
                        isAdminLoggedIn = false;
                        System.out.println("Admin logged out successfully.");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        }
    }

    private static void adminRegister() {
        System.out.println("\n------ Admin Registration ------");
        System.out.print("Enter admin name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter admin email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            System.out.println("All fields are required!");
            return;
        }

        DataTransferHelper<Boolean, String> result = adminController.userRegistration(name, email, password);
        System.out.println("\n" + result.second);
    }

    private static void adminLogin() {
        System.out.println("\n------ Admin Login ------");
        System.out.print("Enter admin email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        if (email.isEmpty() || password.isEmpty()) {
            System.out.println("Both email and password are required!");
            return;
        }

        // In a real system, you would verify credentials here
        isAdminLoggedIn = true;
        DataTransferHelper<Boolean, String> loginResult = adminController.adminLoggin(email, password);
        if (loginResult.first) {
            System.out.println("Admin login successful.");
        } else {
            System.out.println("Admin login failed.");
            adminLogin();
        }
    }

    private static void manageCustomers() {
        while (true) {
            System.out.println("\n------ Customer Management ------");
            System.out.println("1. List All Customers");
            System.out.println("2. Add New Customer");
            System.out.println("3. Update Customer");
            System.out.println("4. Delete Customer");
            System.out.println("0. Back to Admin Menu");
            System.out.print("Select option: ");

            int choice = getIntInput();
            switch (choice) {
                case 1:
                    listCustomers();
                    break;
                case 2:
                    addCustomer();
                    break;
                case 3:
                    updateCustomer();
                    break;
                case 4:
                    deleteCustomer();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void listCustomers() {
        System.out.println("\n------ Customer List ------");
        List<String> customers = customerController.getAllCustomerEmails();
        if (customers.isEmpty()) {
            System.out.println("No customers found.");
            return;
        }

        for (int i = 0; i < customers.size(); i++) {
            String[] details = customerController.getCustomerDetails(customers.get(i));
            System.out.printf("%d. %s - %s - %s - Flagged: %s%n",
                    i + 1, details[0], details[1], details[2], details[3]);
        }
    }

    private static void addCustomer() {
        System.out.println("\n------ Add New Customer ------");
        System.out.print("Enter customer name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter customer email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Enter customer phone: ");
        String phone = scanner.nextLine().trim();

        System.out.print("Flag customer? (y/n): ");
        boolean isFlagged = scanner.nextLine().trim().equalsIgnoreCase("y");

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            System.out.println("All fields are required!");
            return;
        }

        DataTransferHelper<Boolean, String> result = customerController.userRegistration(name, email, phone, isFlagged);
        System.out.println("\n" + result.second);
    }

    private static void updateCustomer() {
        System.out.println("\n------ Update Customer ------");
        listCustomers();
        System.out.print("Enter customer email to update: ");
        String email = scanner.nextLine().trim();

        String[] currentDetails = customerController.getCustomerDetails(email);
        if (currentDetails == null) {
            System.out.println("Customer not found.");
            return;
        }

        System.out.print("Enter new name (" + currentDetails[0] + "): ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            name = currentDetails[0];
        }

        System.out.print("Enter new phone (" + currentDetails[2] + "): ");
        String phone = scanner.nextLine().trim();
        if (phone.isEmpty()) {
            phone = currentDetails[2];
        }

        System.out.print("Flag customer? (current: " + currentDetails[3] + ") (y/n): ");
        boolean isFlagged = scanner.nextLine().trim().equalsIgnoreCase("y");

        DataTransferHelper<Boolean, String> result = customerController.updateCustomer(email, name, phone, isFlagged);
        System.out.println("\n" + result.second);
    }

    private static void deleteCustomer() {
        System.out.println("\n------ Delete Customer ------");
        listCustomers();
        System.out.print("Enter customer email to delete: ");
        String email = scanner.nextLine().trim();

        System.out.print("Are you sure you want to delete this customer? (y/n): ");
        String confirm = scanner.nextLine().trim();
        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("Deletion cancelled.");
            return;
        }

        DataTransferHelper<Boolean, String> result = customerController.deleteCustomer(email);
        System.out.println("\n" + result.second);
    }

    private static void manageTechnicians() {
        while (true) {
            System.out.println("\n------ Technician Management ------");
            System.out.println("1. List All Technicians");
            System.out.println("2. Add New Technician");
            System.out.println("3. Update Technician");
            System.out.println("4. Delete Technician");
            System.out.println("0. Back to Admin Menu");
            System.out.print("Select option: ");

            int choice = getIntInput();
            switch (choice) {
                case 1:
                    listTechnicians();
                    break;
                case 2:
                    addTechnician();
                    break;
                case 3:
                    updateTechnician();
                    break;
                case 4:
                    deleteTechnician();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void listTechnicians() {
        System.out.println("\n------ Technician List ------");
        List<Technician> technicians = techManager.loadAllTechnicians();
        if (technicians.isEmpty()) {
            System.out.println("No technicians found.");
            return;
        }

        for (int i = 0; i < technicians.size(); i++) {
            Technician tech = technicians.get(i);
            System.out.printf("%d. %s - %s - %s - Expertise: %s%n",
                    i + 1, tech.getId(), tech.getName(), tech.getEmail(), tech.getExpertise());
        }
    }

    private static void addTechnician() {
        System.out.println("\n------ Add New Technician ------");
        String id = UUID.randomUUID().toString();
        System.out.println("Generated Technician ID: " + id);

        System.out.print("Enter technician name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter technician email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Enter technician expertise: ");
        String expertise = scanner.nextLine().trim();

        System.out.print("Enter technician expertise: ");
        String password = scanner.nextLine().trim();

        if (name.isEmpty() || email.isEmpty() || expertise.isEmpty()) {
            System.out.println("All fields are required!");
            return;
        }

        Technician technician = new Technician(id, name, email, expertise, password);
        DataTransferHelper<Boolean, String> result = techManager.saveTechnician(technician);
        System.out.println("\n" + result.second);
    }

    private static void updateTechnician() {
        System.out.println("\n------ Update Technician ------");
        listTechnicians();
        System.out.print("Enter technician ID to update: ");
        String id = scanner.nextLine().trim();

        Technician currentTech = techManager.getTechnicianById(id);
        if (currentTech == null) {
            System.out.println("Technician not found.");
            return;
        }

        System.out.print("Enter new name (" + currentTech.getName() + "): ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            name = currentTech.getName();
        }

        System.out.print("Enter new email (" + currentTech.getEmail() + "): ");
        String email = scanner.nextLine().trim();
        if (email.isEmpty()) {
            email = currentTech.getEmail();
        }

        System.out.print("Enter new expertise (" + currentTech.getExpertise() + "): ");
        String expertise = scanner.nextLine().trim();
        if (expertise.isEmpty()) {
            expertise = currentTech.getExpertise();
        }

        System.out.print("Enter new password (********): ");
        String password = scanner.nextLine().trim();
        if (password.isEmpty()) {
            password = currentTech.getPassword();
        }

        Technician updatedTech = new Technician(id, name, email, expertise, password);
        DataTransferHelper<Boolean, String> result = techManager.updateTechnician(updatedTech);
        System.out.println("\n" + result.second);
    }

    private static void deleteTechnician() {
        System.out.println("\n------ Delete Technician ------");
        listTechnicians();
        System.out.print("Enter technician ID to delete: ");
        String id = scanner.nextLine().trim();

        System.out.print("Are you sure you want to delete this technician? (y/n): ");
        String confirm = scanner.nextLine().trim();
        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("Deletion cancelled.");
            return;
        }

        DataTransferHelper<Boolean, String> result = techManager.deleteTechnician(id);
        System.out.println("\n" + result.second);
    }

    private static void manageRepairJobs() {
        while (true) {
            System.out.println("\n------ Repair Job Management ------");
            System.out.println("1. List All Jobs");
            System.out.println("2. View/Edit Job");
            System.out.println("3. Assign Technician");
            System.out.println("4. Manage Job Parts"); // NEW OPTION
            System.out.println("0. Back to Admin Menu");
            System.out.print("Select option: ");

            int choice = getIntInput();
            switch (choice) {
                case 1:
                    listRepairJobs();
                    break;
                case 2:
                    viewEditJob();
                    break;
                case 3:
                    assignTechnician();
                    break;
                case 4:
                    manageJobParts();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void manageJobParts() {
        while (true) {
            System.out.println("\n------ Job Parts Management ------");
            System.out.println("1. List Parts Used in Job");
            System.out.println("2. Add Part to Job");
            System.out.println("3. Remove Part from Job");
            System.out.println("0. Back to Repair Job Menu");
            System.out.print("Select option: ");

            int choice = getIntInput();
            switch (choice) {
                case 1:
                    listPartsInJob();
                    break;
                case 2:
                    addPartToJob();
                    break;
                case 3:
                    removePartFromJob();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void listPartsInJob() {
        System.out.println("\n------ List Parts in Job ------");
        listRepairJobs();
        System.out.print("Enter Job ID: ");
        String jobId = scanner.nextLine().trim();

        List<JobPart> jobParts = repairManager.getPartsByJobId(jobId);
        if (jobParts.isEmpty()) {
            System.out.println("No parts used in this job.");
            return;
        }

        System.out.println("\nParts used in Job " + jobId + ":");
        for (int i = 0; i < jobParts.size(); i++) {
            SparePart part = sparePartsManager.getSparePartById(jobParts.get(i).getPartId());
            System.out.printf("%d. %s - %s (Cost: $%.2f)%n",
                    i + 1, part.getPartId(), part.getName(), part.getCost());
        }
    }

    private static void addPartToJob() {
        System.out.println("\n------ Add Part to Job ------");
        listRepairJobs();
        System.out.print("Enter Job ID: ");
        String jobId = scanner.nextLine().trim();

        listSpareParts();
        System.out.print("Enter Part ID to add: ");
        String partId = scanner.nextLine().trim();

        JobPart jobPart = new JobPart(jobId, partId);
        DataTransferHelper<Boolean, String> result = repairManager.saveJobPart(jobPart);
        System.out.println("\n" + result.second);
    }

    private static void removePartFromJob() {
        System.out.println("\n------ Remove Part from Job ------");
        listRepairJobs();
        System.out.print("Enter Job ID: ");
        String jobId = scanner.nextLine().trim();

        List<JobPart> jobParts = repairManager.getPartsByJobId(jobId);
        if (jobParts.isEmpty()) {
            System.out.println("No parts used in this job.");
            return;
        }

        System.out.println("\nParts used in Job " + jobId + ":");
        for (int i = 0; i < jobParts.size(); i++) {
            SparePart part = sparePartsManager.getSparePartById(jobParts.get(i).getPartId());
            System.out.printf("%d. %s - %s%n", i + 1, part.getPartId(), part.getName());
        }

        System.out.print("Enter Part ID to remove: ");
        String partId = scanner.nextLine().trim();

        DataTransferHelper<Boolean, String> result = repairManager.removeJobPart(jobId, partId);
        System.out.println("\n" + result.second);
    }

    private static void listRepairJobs() {
        System.out.println("\n------ Repair Job List ------");
        List<RepairWork> jobs = repairManager.loadAllRepairWorks();
        if (jobs.isEmpty()) {
            System.out.println("No repair jobs found.");
            return;
        }

        for (int i = 0; i < jobs.size(); i++) {
            RepairWork job = jobs.get(i);
            System.out.printf("%d. %s - Customer: %s - Equipment: %s - Status: %s - Technician: %s%n",
                    i + 1, job.getJobId(), job.getCustomerId(), job.getEquipment(),
                    job.getStatusString(), job.getTechnicianId() != null ? job.getTechnicianId() : "Not assigned");
        }
    }

    private static void viewEditJob() {
        System.out.println("\n------ View/Edit Job ------");
        listRepairJobs();
        System.out.print("Enter job ID to view/edit: ");
        String jobId = scanner.nextLine().trim();

        RepairWork job = repairManager.getRepairWorkById(jobId);
        if (job == null) {
            System.out.println("Job not found.");
            return;
        }

        System.out.println("\nCurrent Job Details:");
        System.out.println("Job ID: " + job.getJobId());
        System.out.println("Customer ID: " + job.getCustomerId());
        System.out.println("Technician ID: " + (job.getTechnicianId() != null ? job.getTechnicianId() : "Not assigned"));
        System.out.println("Equipment: " + job.getEquipment());
        System.out.println("Issue: " + job.getIssue());
        System.out.println("Status: " + job.getStatusString());

        System.out.println("\nEnter new values (leave blank to keep current):");
        System.out.print("Equipment (" + job.getEquipment() + "): ");
        String equipment = scanner.nextLine().trim();
        if (equipment.isEmpty()) {
            equipment = job.getEquipment();
        }

        System.out.print("Issue (" + job.getIssue() + "): ");
        String issue = scanner.nextLine().trim();
        if (issue.isEmpty()) {
            issue = job.getIssue();
        }

        System.out.println("Available Statuses:");
        for (Status status : Status.values()) {
            System.out.println("- " + status);
        }
        System.out.print("Status (" + job.getStatusString() + "): ");
        String statusStr = scanner.nextLine().trim();
        Status status = statusStr.isEmpty() ? job.getStatus() : Status.valueOf(statusStr);

        job.setEquipment(equipment);
        job.setIssue(issue);
        job.setStatus(status);

        DataTransferHelper<Boolean, String> result = repairManager.updateRepairWork(job);
        System.out.println("\n" + result.second);
    }

    private static void assignTechnician() {
        System.out.println("\n------ Assign Technician ------");
        listRepairJobs();
        System.out.print("Enter job ID to assign technician: ");
        String jobId = scanner.nextLine().trim();

        RepairWork job = repairManager.getRepairWorkById(jobId);
        if (job == null) {
            System.out.println("Job not found.");
            return;
        }

        listTechnicians();
        System.out.print("Enter technician ID to assign: ");
        String techId = scanner.nextLine().trim();

        Technician tech = techManager.getTechnicianById(techId);
        if (tech == null) {
            System.out.println("Technician not found.");
            return;
        }

        job.setTechnicianId(techId);
        DataTransferHelper<Boolean, String> result = repairManager.updateRepairWork(job);
        System.out.println("\n" + result.second);
    }

    private static void manageSpareParts() {
        while (true) {
            System.out.println("\n------ Spare Parts Management ------");
            System.out.println("1. List All Spare Parts");
            System.out.println("2. Add New Spare Part");
            System.out.println("3. Update Spare Part");
            System.out.println("4. Delete Spare Part");
            System.out.println("0. Back to Admin Menu");
            System.out.print("Select option: ");

            int choice = getIntInput();
            switch (choice) {
                case 1:
                    listSpareParts();
                    break;
                case 2:
                    addSparePart();
                    break;
                case 3:
                    updateSparePart();
                    break;
                case 4:
                    deleteSparePart();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void listSpareParts() {
        System.out.println("\n------ Spare Parts List ------");
        List<SparePart> parts = sparePartsManager.loadAllSpareParts();
        if (parts.isEmpty()) {
            System.out.println("No spare parts found.");
            return;
        }

        for (int i = 0; i < parts.size(); i++) {
            SparePart part = parts.get(i);
            System.out.printf("%d. %s - %s - Cost: $%.2f - Location: %s%n",
                    i + 1, part.getPartId(), part.getName(), part.getCost(), part.getLocation());
        }
    }

    private static void addSparePart() {
        System.out.println("\n------ Add New Spare Part ------");
        String partId = UUID.randomUUID().toString();
        System.out.println("Generated Part ID: " + partId);

        System.out.print("Enter part name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter part cost: ");
        double cost = getDoubleInput();

        System.out.print("Enter part location: ");
        String location = scanner.nextLine().trim();

        if (name.isEmpty() || location.isEmpty()) {
            System.out.println("All fields are required!");
            return;
        }

        SparePart sparePart = new SparePart(partId, name, cost, location);
        DataTransferHelper<Boolean, String> result = sparePartsManager.saveSparePart(sparePart);
        System.out.println("\n" + result.second);
    }

    private static void updateSparePart() {
        System.out.println("\n------ Update Spare Part ------");
        listSpareParts();
        System.out.print("Enter part ID to update: ");
        String partId = scanner.nextLine().trim();

        SparePart currentPart = sparePartsManager.getSparePartById(partId);
        if (currentPart == null) {
            System.out.println("Spare part not found.");
            return;
        }

        System.out.print("Enter new name (" + currentPart.getName() + "): ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            name = currentPart.getName();
        }

        System.out.print("Enter new cost (" + currentPart.getCost() + "): ");
        String costStr = scanner.nextLine().trim();
        double cost = costStr.isEmpty() ? currentPart.getCost() : Double.parseDouble(costStr);

        System.out.print("Enter new location (" + currentPart.getLocation() + "): ");
        String location = scanner.nextLine().trim();
        if (location.isEmpty()) {
            location = currentPart.getLocation();
        }

        SparePart updatedPart = new SparePart(partId, name, cost, location);
        DataTransferHelper<Boolean, String> result = sparePartsManager.updateSparePart(updatedPart);
        System.out.println("\n" + result.second);
    }

    private static void deleteSparePart() {
        System.out.println("\n------ Delete Spare Part ------");
        listSpareParts();
        System.out.print("Enter part ID to delete: ");
        String partId = scanner.nextLine().trim();

        System.out.print("Are you sure you want to delete this spare part? (y/n): ");
        String confirm = scanner.nextLine().trim();
        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("Deletion cancelled.");
            return;
        }

        DataTransferHelper<Boolean, String> result = sparePartsManager.deleteSparePart(partId);
        System.out.println("\n" + result.second);
    }

    private static void technicianMenu() {
        while (true) {
            System.out.println("\n------ Technician Menu ------");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("0. Back to Main Menu");
            System.out.print("Select option: ");

            int choice = getIntInput();
            switch (choice) {
                case 1:
                    technicianRegister();
                    break;
                case 2:
                    technicianLogin();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void technicianRegister() {
        System.out.println("\n------ Technician Registration ------");

        // Generate ID automatically
        String id = UUID.randomUUID().toString().substring(0, 8);
        System.out.println("Your Technician ID: " + id);

        System.out.print("Enter your full name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter your email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Enter your expertise: ");
        String expertise = scanner.nextLine().trim();

        System.out.print("Create a password: ");
        String password = scanner.nextLine().trim();

        // Validate inputs
        if (name.isEmpty() || email.isEmpty() || expertise.isEmpty() || password.isEmpty()) {
            System.out.println("All fields are required!");
            return;
        }

        // Create technician object
        Technician newTechnician = new Technician(id, name, email, expertise, password);

        // Save to CSV
        DataTransferHelper<Boolean, String> result = techManager.saveTechnician(newTechnician);
        System.out.println("\n" + result.second);
    }

    private static void technicianLogin() {
        System.out.println("\n------ Technician Login ------");

        System.out.print("Enter your email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Enter your password: ");
        String password = scanner.nextLine().trim();

        if (email.isEmpty() || password.isEmpty()) {
            System.out.println("Both email and password are required!");
            return;
        }

        // Check credentials
        DataTransferHelper<Boolean, String> loginResult = techController.loginTechnician(email, password);

        if (loginResult.first) {
            System.out.println("\nLogin successful!");
            // Proceed to technician dashboard
            technicianDashboard(email);
        } else {
            System.out.println("\nLogin failed: " + loginResult.second);
        }
    }

    private static void technicianDashboard(String technicianEmail) {
        Technician technician = techManager.getTechnicianByEmail(technicianEmail);
        if (technician == null) {
            System.out.println("Technician data not found!");
            return;
        }

        while (true) {
            System.out.println("\n------ Technician Dashboard ------");
            System.out.println("Logged in as: " + technician.getName() + " (" + technician.getEmail() + ")");
            System.out.println("1. View Assigned Jobs");
            System.out.println("2. Update Job Status");
            System.out.println("3. Manage Job Parts"); // NEW OPTION
            System.out.println("4. View Profile");
            System.out.println("0. Logout");
            System.out.print("Select option: ");

            int choice = getIntInput();
            switch (choice) {
                case 1:
                    viewAssignedJobs(technician.getId());
                    break;
                case 2:
                    updateJobStatus(technician.getId());
                    break;
                case 3: // NEW: Manage Job Parts
                    manageJobPartsTechnician(technician.getId());
                    break;
                case 4:
                    viewTechnicianProfile(technician);
                    break;
                case 0:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void manageJobPartsTechnician(String technicianId) {
        while (true) {
            System.out.println("\n------ Manage Job Parts ------");
            System.out.println("1. List Parts in Job");
            System.out.println("2. Add Part to Job");
            System.out.println("0. Back to Dashboard");
            System.out.print("Select option: ");

            int choice = getIntInput();
            switch (choice) {
                case 1:
                    listPartsInJobTechnician(technicianId);
                    break;
                case 2:
                    addPartToJobTechnician(technicianId);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

   private static void listPartsInJobTechnician(String technicianId) {
    System.out.println("\n------ List Parts in Your Job ------");
    List<RepairWork> assignedJobs = repairManager.loadAllRepairWorks()
            .stream()
            .filter(job -> technicianId.equals(job.getTechnicianId()))
            .collect(Collectors.toList());

    if (assignedJobs.isEmpty()) {
        System.out.println("No jobs assigned to you.");
        return;
    }

    for (RepairWork job : assignedJobs) {
        System.out.println("\nJob ID: " + job.getJobId() + " - " + job.getEquipment());
        List<JobPart> parts = repairManager.getPartsByJobId(job.getJobId());
        
        if (parts.isEmpty()) {
            System.out.println("No parts used in this job.");
        } else {
            for (JobPart part : parts) {
                SparePart sparePart = sparePartsManager.getSparePartById(part.getPartId());
                
                if (sparePart != null) {
                    System.out.println("- " + sparePart.getName() + " (" + sparePart.getPartId() + ")");
                } else {
                    logHelper.writeLog("- [Part not found] (ID: " + part.getPartId() + ")");
                    System.out.println("- [Part not found] (ID: " + part.getPartId() + ")");
                }
            }
        }
    }
}
    private static void addPartToJobTechnician(String technicianId) {
        System.out.println("\n------ Add Part to Your Job ------");
        List<RepairWork> assignedJobs = repairManager.loadAllRepairWorks()
                .stream()
                .filter(job -> technicianId.equals(job.getTechnicianId()))
                .collect(Collectors.toList());

        if (assignedJobs.isEmpty()) {
            System.out.println("No jobs assigned to you.");
            return;
        }

        System.out.println("\nYour Assigned Jobs:");
        for (int i = 0; i < assignedJobs.size(); i++) {
            System.out.printf("%d. %s - %s%n",
                    i + 1, assignedJobs.get(i).getJobId(), assignedJobs.get(i).getEquipment());
        }

        System.out.print("Select job (number): ");
        int jobChoice = getIntInput() - 1;
        if (jobChoice < 0 || jobChoice >= assignedJobs.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        String jobId = assignedJobs.get(jobChoice).getJobId();
        listSpareParts();
        System.out.print("Enter Part ID to add: ");
        String partId = scanner.nextLine().trim();

        JobPart jobPart = new JobPart(jobId, partId);
        DataTransferHelper<Boolean, String> result = repairManager.saveJobPart(jobPart);
        System.out.println("\n" + result.second);
    }

    private static void viewAssignedJobs(String technicianId) {
        List<RepairWork> jobs = repairManager.loadAllRepairWorks()
                .stream()
                .filter(job -> technicianId.equals(job.getTechnicianId()))
                .collect(Collectors.toList());

        if (jobs.isEmpty()) {
            System.out.println("\nNo jobs assigned to you.");
            return;
        }

        System.out.println("\n------ Your Assigned Jobs ------");
        for (int i = 0; i < jobs.size(); i++) {
            RepairWork job = jobs.get(i);
            System.out.printf("%d. %s - %s - Status: %s%n",
                    i + 1, job.getJobId(), job.getEquipment(), job.getStatusString());
        }
    }

    private static void updateJobStatus(String technicianId) {
        viewAssignedJobs(technicianId);

        System.out.print("\nEnter job ID to update: ");
        String jobId = scanner.nextLine().trim();

        RepairWork job = repairManager.getRepairWorkById(jobId);
        if (job == null || !technicianId.equals(job.getTechnicianId())) {
            System.out.println("Job not found or not assigned to you!");
            return;
        }

        System.out.println("\nCurrent Status: " + job.getStatusString());
        System.out.println("Available Statuses:");
        for (Status status : Status.values()) {
            System.out.println("- " + status);
        }

        System.out.print("Enter new status: ");
        String newStatus = scanner.nextLine().trim();

        try {
            job.setStatus(Status.valueOf(newStatus));
            DataTransferHelper<Boolean, String> result = repairManager.updateRepairWork(job);
            System.out.println("\n" + result.second);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid status entered!");
            logHelper.writeLog("Invalid status entered!. Exception: " + e.getMessage());
        }
    }

    private static void viewTechnicianProfile(Technician technician) {
        System.out.println("\n------ Your Profile ------");
        System.out.println("ID: " + technician.getId());
        System.out.println("Name: " + technician.getName());
        System.out.println("Email: " + technician.getEmail());
        System.out.println("Expertise: " + technician.getExpertise());
    }

    private static void createRepairJob() {
        System.out.println("\n------ Create New Repair Job ------");
        System.out.print("Enter customer ID: ");
        String customerId = scanner.nextLine().trim();

        System.out.print("Enter equipment name: ");
        String equipment = scanner.nextLine().trim();

        System.out.print("Enter issue description: ");
        String issue = scanner.nextLine().trim();

        if (customerId.isEmpty() || equipment.isEmpty() || issue.isEmpty()) {
            System.out.println("All fields are required!");
            return;
        }

        String jobId = "JOB" + System.currentTimeMillis();
        RepairWork newJob = new RepairWork(jobId, customerId, null, equipment, issue, Status.JOB_CREATED);

        DataTransferHelper<Boolean, String> result = repairManager.saveRepairWork(newJob);
        System.out.println("\n" + result.second);
        if (result.first) {
            System.out.println("Created Job ID: " + jobId);
        }
    }

    private static void assessUpdateJob() {
        System.out.println("\n------ Assess/Update Job ------");
        listRepairJobs();
        System.out.print("Enter job ID to assess/update: ");
        String jobId = scanner.nextLine().trim();

        RepairWork job = repairManager.getRepairWorkById(jobId);
        if (job == null) {
            System.out.println("Job not found.");
            return;
        }

        System.out.println("\nCurrent Job Details:");
        System.out.println("Job ID: " + job.getJobId());
        System.out.println("Customer ID: " + job.getCustomerId());
        System.out.println("Equipment: " + job.getEquipment());
        System.out.println("Issue: " + job.getIssue());
        System.out.println("Current Status: " + job.getStatusString());

        System.out.println("\nAvailable Statuses:");
        for (Status status : Status.values()) {
            System.out.println("- " + status);
        }
        System.out.print("Enter new status: ");
        String statusStr = scanner.nextLine().trim();
        Status status = Status.valueOf(statusStr);

        System.out.print("Enter repair tasks/notes: ");
        String tasks = scanner.nextLine().trim();

        job.setStatus(status);
        job.updateIssue(tasks);

        DataTransferHelper<Boolean, String> result = repairManager.updateRepairWork(job);
        System.out.println("\n" + result.second);
    }

    private static void deleteJob() {
        System.out.println("\n------ Delete Job ------");
        listRepairJobs();
        System.out.print("Enter job ID to delete: ");
        String jobId = scanner.nextLine().trim();

        System.out.print("Are you sure you want to delete this job? (y/n): ");
        String confirm = scanner.nextLine().trim();
        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("Deletion cancelled.");
            return;
        }

        DataTransferHelper<Boolean, String> result = repairManager.deleteRepairWork(jobId);
        System.out.println("\n" + result.second);
    }

    private static int getIntInput() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
                logHelper.writeLog("Invalid input. Please enter a number. Exception: " + e.getMessage());
            }
        }
    }

    private static double getDoubleInput() {
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
                logHelper.writeLog("Invalid input. Please enter a number. Exception: " + e.getMessage());
            }
        }
    }

    private static void sendCustomerNotification() {
        System.out.println("\n------ Send Customer Notification ------");
        listCustomers();
        System.out.print("Enter customer email to notify: ");
        String customerId = scanner.nextLine().trim();

        System.out.print("Enter notification message: ");
        String message = scanner.nextLine().trim();

        if (customerId.isEmpty() || message.isEmpty()) {
            System.out.println("Both email and message are required!");
            return;
        }

        DataTransferHelper<Boolean, String> result = notificationHelper.addNotification(customerId, message);
        System.out.println("\n" + result.second);
    }
}
