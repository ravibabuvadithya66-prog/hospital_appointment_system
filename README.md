# Hospital Appointment System — FSD-II Mini Project

Project folder: `hospital_appointment_system`  
Technology: Java 21, JSP, Jakarta Servlet, JDBC, MySQL, CSS  
Server: Apache Tomcat 10.1+ (Tomcat 11 may also work with a compatible setup)

## Features
- Display doctors and specializations
- Book appointment with patient name, phone, email, doctor, date, time, and reason
- Save records to MySQL database
- View appointments and cancel appointments
- Validate required fields and 10-digit phone numbers
- Prevent duplicate doctor/date/time bookings using a database unique constraint

## Setup
1. Install JDK 21, IntelliJ IDEA, MySQL Server, and Apache Tomcat 10.1 or newer.
2. Start MySQL and execute `database/hospital_appointment_system.sql` in MySQL Workbench or phpMyAdmin.
3. Open `src/main/java/com/carepoint/DBUtil.java`. Default connection is user `root`, empty password. Change `PASSWORD` to your local MySQL password if required.
4. In IntelliJ, choose **File → Open** and select this folder (the folder containing `pom.xml`). Load the Maven project.
5. Set Project SDK to JDK 21.
6. IntelliJ Ultimate: Run → Edit Configurations → + → Tomcat Server → Local. Set Tomcat home, add artifact `hospital-appointment-system:war exploded`, context `/hospital-appointment-system`, then Run.
7. Open `http://localhost:8080/hospital-appointment-system/appointments`.

### IntelliJ Community edition
Tomcat integration may require a plugin. Alternatively, in the project terminal run `mvn clean package`, copy `target/hospital-appointment-system.war` into Tomcat's `webapps` folder, start Tomcat, and visit the URL above. Maven dependencies need internet on the first build.

## Troubleshooting
- Access denied: update MySQL username/password in DBUtil.java.
- Unknown database/table: run the SQL file.
- Connection refused: start MySQL and verify port 3306.
- 404: check that the WAR is deployed with context `/hospital-appointment-system`.

## File overview
- `AppointmentServlet.java`: reads, creates, and cancels appointments.
- `DBUtil.java`: JDBC database connection.
- `appointments.jsp`: booking form and appointment table.
- `style.css`: page styling.
- `database/hospital_appointment_system.sql`: database schema and sample doctors.

Educational demo only; no authentication and not suitable for real patient data.
