1.Name of project: user-balance-app

2.Launch of project:
2.1 Application:
In terminal: mvn spring-boot:run
or main method in class UserBalanceApplication
2.2. Database, Elasticsearch and Redis:
docker compose up --build

3.Port of the project:
http://localhost:8081

4.Names/emails/passwords/phones of users in database:
4.1. Alice/alice@example.com/12345678/123456789012
4.2. Bob/bob@example.com/super-password/109876543210

5.Configuration: resources/application.yaml

6.Database PostgreSQL connection:
Name: userbalancedb@localhost
User: test
Password: test
Port: 5432

7.Rest controllers:
AuthenticationController:
registration(POST): http://localhost:8081/api/v1/auth/signup + body;
authentication(POST): http://localhost:8081/api/v1/auth/signin + body

UserController:
getById(GET): http://localhost:8081/api/v1/users/{userId};
searchUsers(GET): http://localhost:8081/api/v1/users/search + parameters (by default - all users)
addEmail(POST): http://localhost:8081/api/v1/users/email + body
updateEmail(PUT): http://localhost:8081/api/v1/users/email/{emailId} + body;
deleteEmail(DELETE): http://localhost:8081/api/v1/users/email/{emailId}
addPhone(POST): http://localhost:8081/api/v1/users/phone + body
updatePhone(PUT): http://localhost:8081/api/v1/users/phone/{phoneId} + body;
deletePhone(DELETE): http://localhost:8081/api/v1/users/phone/{phoneId}

BalanceController:
getBalanceForCurrentUser(GET): http://localhost:8081/api/v1/balance;
transferMoney(POST): http://localhost:8081/api/v1/balance/transfer + parameters;
accrueAllBalances(POST): http://localhost:8081/api/v1/balance/accrue (This method needs if you want manually start to charge interest)