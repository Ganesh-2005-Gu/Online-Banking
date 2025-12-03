# 💳 SecureBank Online - Modern Banking Application

A feature-rich, secure online banking system built with Java EE, featuring a stunning modern UI with animated backgrounds, glassmorphism effects, and real-time transaction processing.

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Jakarta EE](https://img.shields.io/badge/Jakarta%20EE-007396?style=for-the-badge&logo=java&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Apache Tomcat](https://img.shields.io/badge/Apache%20Tomcat-F8DC75?style=for-the-badge&logo=apache-tomcat&logoColor=black)

## ✨ Features

### 🔐 User Authentication & Authorization
- Secure user registration and login
- **Multi-Factor Authentication (MFA)**: Support for TOTP (Google Authenticator) and Email OTP
- Role-based access control (USER/ADMIN)
- Session management with timeout

### 💰 Banking Operations
- **Account Management**: Create multiple accounts (Savings/Current)
- **Deposits**: Add funds to accounts
- **Withdrawals**: Withdraw with balance validation
- **Transfers**: Transfer money between accounts with **ACID Compliance**
- **Transaction History**: View detailed transaction logs
- **Audit Logging**: Comprehensive tracking of all transaction states (PENDING, COMPLETED, FAILED)

### 👑 Admin Features
- User management dashboard
- Account search and monitoring
- System-wide user overview

### 🎨 Modern UI/UX
- **Unique Page Designs**: Each page has distinct color schemes and backgrounds
- **Animated Gradients**: Smooth color transitions (10-20s cycles)
- **Floating Particles**: 50 animated particles per page
- **Glassmorphism Cards**: Frosted glass effect with backdrop blur
- **Responsive Design**: Works on all screen sizes
- **Smooth Animations**: Hover effects, transitions, and micro-interactions

## 🛠️ Technology Stack

### Backend
- **Java 17+**
- **Jakarta EE 10** (Servlets, JSP, JSTL)
- **Apache Tomcat 10.1.49**
- **Maven** - Build automation
- **JavaMail API** - Email notifications
- **Commons Codec** - TOTP generation

### Database
- **MySQL 8.0+**
- **JDBC** - Database connectivity

### Frontend
- **HTML5 & CSS3**
- **JavaScript (Vanilla)**
- **Google Fonts (Inter)**
- **Custom CSS Animations**

## 📁 Project Structure

```
online-bank/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── banking/
│       │           ├── dao/                    # Data Access Objects
│       │           │   ├── AccountDAO.java     # Account operations
│       │           │   ├── TransactionDAO.java # Transaction operations
│       │           │   └── UserDAO.java        # User operations
│       │           ├── listener/               # Application listeners
│       │           │   └── StartupListener.java # App initialization
│       │           ├── model/                  # Domain models
│       │           │   ├── Account.java        # Account entity
│       │           │   ├── Transaction.java    # Transaction entity
│       │           │   └── User.java           # User entity
│       │           ├── servlet/                # HTTP Servlets
│       │           │   ├── AdminServlet.java   # Admin operations
│       │           │   ├── CreateAccountServlet.java
│       │           │   ├── DashboardServlet.java
│       │           │   ├── HealthCheckServlet.java
│       │           │   ├── HistoryServlet.java
│       │           │   ├── LoginServlet.java
│       │           │   ├── LogoutServlet.java
│       │           │   ├── RegisterServlet.java
│       │           │   └── TransactionServlet.java
│       │           └── util/                   # Utility classes
│       │               ├── DBConnection.java   # Database connection
│       │               └── DatabaseInitializer.java # Auto DB setup
│       └── webapp/
│           ├── css/
│           │   ├── style.css                   # Main stylesheet
│           │   ├── login-bg.png               # Login background
│           │   ├── register-bg.png            # Register background
│           │   ├── transaction-bg.png         # Dashboard background
│           │   ├── history-bg.png             # History background
│           │   ├── admin-bg.png               # Admin background
│           │   └── banking-icon.png           # Card watermark
│           ├── META-INF/
│           │   └── context.xml                # Context configuration
│           ├── WEB-INF/
│           │   └── web.xml                    # Deployment descriptor
│           ├── admin_dashboard.jsp            # Admin panel
│           ├── create_account.jsp             # Account creation
│           ├── dashboard.jsp                  # Main dashboard
│           ├── history.jsp                    # Transaction history
│           ├── index.jsp                      # Login page
│           └── register.jsp                   # Registration page
├── banking.sql                                # Database schema
├── pom.xml                                    # Maven configuration
└── README.md                                  # This file
```

## 🚀 Getting Started

### Prerequisites
- **Java JDK 17+**
- **Apache Tomcat 10.1+**
- **MySQL 8.0+**
- **Maven 3.6+**

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd online-bank
   ```

2. **Configure Database**
   - Update database credentials in `src/main/java/com/banking/util/DBConnection.java`
   ```java
   private static final String URL = "jdbc:mysql://localhost:3306/banking_system";
   private static final String USER = "root";
   private static final String PASSWORD = "your_password";
   ```

3. **Build the project**
   ```bash
   mvn clean package
   ```

4. **Deploy to Tomcat**
   - Copy `target/online-banking.war` to Tomcat's `webapps` directory
   - Start Tomcat server

5. **Access the application**
   ```
   http://localhost:8080/online-banking/
   ```

### Database Auto-Initialization

The application automatically creates the database and tables on first startup using `DatabaseInitializer.java`. No manual SQL execution required!

**Default Admin Account:**
- Username: `admin`
- Password: `admin123`

## 🎨 Page Color Schemes

Each page has a unique visual identity:

| Page | Colors | Animation Speed |
|------|--------|----------------|
| 🔐 Login | Purple → Violet → Pink | 15s |
| ✍️ Register | Emerald Green → Teal | 12s |
| 💰 Dashboard | Hot Pink → Orange → Gold | 18s |
| 🏦 Create Account | Orange → Gold → Blue | 14s |
| 📜 History | Ocean Blue → Cyan | 20s |
| 👑 Admin | Dark Red → Purple | 10s |

## 🔒 Security Features

- Password validation
- Session timeout (30 minutes)
- SQL injection prevention (PreparedStatements)
- Role-based access control
- HTTPS ready (configure in Tomcat)

## 📊 Database Schema

### Users Table
- `id` (INT, PK, AUTO_INCREMENT)
- `username` (VARCHAR, UNIQUE)
- `password` (VARCHAR)
- `full_name` (VARCHAR)
- `email` (VARCHAR)
- `role` (ENUM: USER/ADMIN)
- `mfa_enabled` (BOOLEAN)
- `mfa_secret` (VARCHAR)
- `mfa_method` (ENUM: TOTP/EMAIL/NONE)
- `created_at` (TIMESTAMP)

### Accounts Table
- `account_number` (VARCHAR, PK)
- `user_id` (INT, FK)
- `balance` (DECIMAL)
- `account_type` (ENUM: SAVINGS/CURRENT)
- `created_at` (TIMESTAMP)

### Transactions Table
- `id` (INT, PK, AUTO_INCREMENT)
- `account_number` (VARCHAR, FK)
- `type` (ENUM: DEPOSIT/WITHDRAWAL/TRANSFER)
- `amount` (DECIMAL)
- `related_account` (VARCHAR)
- `status` (ENUM: PENDING/COMPLETED/FAILED/ROLLED_BACK)
- `timestamp` (TIMESTAMP)

### Transaction Audit Log
- `id` (INT, PK, AUTO_INCREMENT)
- `transaction_id` (INT, FK)
- `action` (VARCHAR)
- `details` (TEXT)
- `timestamp` (TIMESTAMP)

### OTP Verification
- `id` (INT, PK, AUTO_INCREMENT)
- `user_id` (INT, FK)
- `otp_code` (VARCHAR)
- `type` (ENUM: LOGIN/TRANSACTION)
- `expires_at` (TIMESTAMP)
- `created_at` (TIMESTAMP)

## 🧪 Testing

### Health Check Endpoint
```
http://localhost:8080/online-banking/health
```
Returns "OK" if the application is running.

### Test User Registration
1. Navigate to registration page
2. Fill in user details
3. Submit form
4. Login with new credentials

## 🐛 Troubleshooting

### 405 Method Not Allowed
- Ensure you've rebuilt after recent updates
- Clear Tomcat's work directory
- Redeploy the WAR file

### Database Connection Issues
- Verify MySQL is running
- Check credentials in `DBConnection.java`
- Ensure MySQL JDBC driver is in dependencies

### 404 Not Found
- Check context path in `META-INF/context.xml`
- Verify WAR file name matches context path
- Check Tomcat logs for deployment errors

## 📝 API Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/login` | POST | User authentication |
| `/register` | POST | User registration |
| `/logout` | GET | User logout |
| `/dashboard` | GET | Main dashboard |
| `/transaction` | POST | Process transactions |
| `/history` | GET | Transaction history |
| `/createAccount` | POST | Create new account |
| `/mfa-setup` | GET/POST | MFA enrollment |
| `/mfa-verify` | GET/POST | MFA verification |
| `/admin` | GET | Admin dashboard |
| `/health` | GET | Health check |

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License.

## 👨‍💻 Author

Created with ❤️ for modern banking solutions

## 🙏 Acknowledgments

- Jakarta EE community
- Apache Tomcat team
- Google Fonts (Inter)
- Modern UI/UX design principles

---

**Note:** This is a demonstration project. For production use, implement additional security measures including password hashing, HTTPS, CSRF protection, and comprehensive input validation.
