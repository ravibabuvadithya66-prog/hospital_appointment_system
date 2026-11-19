
package com.carepoint;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/appointments")
public class AppointmentServlet extends HttpServlet {

 public record Doctor(int id, String name, String specialty) {}

 public record Appointment(
         int id,
         String patientName,
         String phone,
         String email,
         LocalDate appointmentDate,
         String appointmentTime,
         String reason,
         String doctorName,
         String specialty
 ) {}

 @Override
 protected void doGet(HttpServletRequest req, HttpServletResponse res)
         throws ServletException, IOException {

  String del = req.getParameter("delete");

  if (del != null) {
   try {
    int id = Integer.parseInt(del);

    try (Connection c = DBUtil.getConnection();
         PreparedStatement p = c.prepareStatement(
                 "DELETE FROM appointments WHERE id = ?")) {

     p.setInt(1, id);
     p.executeUpdate();
    }

    res.sendRedirect(
            req.getContextPath() + "/appointments?message=cancelled");
    return;

   } catch (Exception e) {
    req.setAttribute("error", "Could not cancel appointment.");
   }
  }

  List<Doctor> doctors = new ArrayList<>();
  List<Appointment> appointments = new ArrayList<>();

  try (Connection c = DBUtil.getConnection()) {

   String doctorSql =
           "SELECT id, name, specialty FROM doctors ORDER BY name";

   try (PreparedStatement p = c.prepareStatement(doctorSql);
        ResultSet r = p.executeQuery()) {

    while (r.next()) {
     doctors.add(new Doctor(
             r.getInt("id"),
             r.getString("name"),
             r.getString("specialty")
     ));
    }
   }

   String appointmentSql =
           "SELECT a.id, a.patient_name, a.phone, a.email, " +
                   "a.appointment_date, a.appointment_time, a.reason, " +
                   "d.name AS doctor_name, d.specialty " +
                   "FROM appointments a " +
                   "JOIN doctors d ON a.doctor_id = d.id " +
                   "ORDER BY a.appointment_date, a.appointment_time";

   try (PreparedStatement p = c.prepareStatement(appointmentSql);
        ResultSet r = p.executeQuery()) {

    while (r.next()) {
     appointments.add(new Appointment(
             r.getInt("id"),
             r.getString("patient_name"),
             r.getString("phone"),
             r.getString("email"),
             r.getDate("appointment_date").toLocalDate(),
             r.getString("appointment_time"),
             r.getString("reason"),
             r.getString("doctor_name"),
             r.getString("specialty")
     ));
    }
   }

  } catch (SQLException e) {
   getServletContext().log("Database error while loading appointments", e);

   req.setAttribute(
           "error",
           "Cannot connect to MySQL. Start MySQL, execute the SQL file, " +
                   "and check the credentials in DBUtil.java."
   );
  }

  req.setAttribute("doctors", doctors);
  req.setAttribute("appointments", appointments);

  req.getRequestDispatcher("/WEB-INF/views/appointments.jsp")
          .forward(req, res);
 }

 @Override
 protected void doPost(HttpServletRequest req, HttpServletResponse res)
         throws ServletException, IOException {

  req.setCharacterEncoding("UTF-8");

  String name = v(req, "patientName");
  String phone = v(req, "phone");
  String email = v(req, "email");
  String doctor = v(req, "doctorId");
  String date = v(req, "appointmentDate");
  String time = v(req, "appointmentTime");
  String reason = v(req, "reason");

  if (name.length() < 2
          || !phone.matches("\\d{10}")
          || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
          || doctor.isEmpty()
          || date.isEmpty()
          || time.isEmpty()) {

   res.sendRedirect(
           req.getContextPath() + "/appointments?message=invalid");
   return;
  }

  try {
   LocalDate appointmentDate = LocalDate.parse(date);

   if (appointmentDate.isBefore(LocalDate.now())) {
    res.sendRedirect(
            req.getContextPath() + "/appointments?message=past");
    return;
   }

   String sql =
           "INSERT INTO appointments " +
                   "(patient_name, phone, email, doctor_id, " +
                   "appointment_date, appointment_time, reason) " +
                   "VALUES (?, ?, ?, ?, ?, ?, ?)";

   try (Connection c = DBUtil.getConnection();
        PreparedStatement p = c.prepareStatement(sql)) {

    p.setString(1, name);
    p.setString(2, phone);
    p.setString(3, email);
    p.setInt(4, Integer.parseInt(doctor));

    // Fix: explicitly use java.sql.Date
    p.setDate(5, java.sql.Date.valueOf(appointmentDate));

    p.setString(6, time);
    p.setString(7, reason);

    p.executeUpdate();
   }

   res.sendRedirect(
           req.getContextPath() + "/appointments?message=booked");

  } catch (SQLIntegrityConstraintViolationException e) {
   res.sendRedirect(
           req.getContextPath() + "/appointments?message=slot");

  } catch (Exception e) {
   getServletContext().log("Error while booking appointment", e);

   res.sendRedirect(
           req.getContextPath() + "/appointments?message=error");
  }
 }

 private String v(HttpServletRequest req, String key) {
  String value = req.getParameter(key);
  return value == null ? "" : value.trim();
 }
}

