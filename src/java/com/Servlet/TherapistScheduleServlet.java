package com.Servlet;

import com.Dao.TherapistDao;
import com.Dao.TherapistScheduleDao;
import com.Model.Therapist;
import com.Model.TherapistSchedule;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;


public class TherapistScheduleServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TherapistScheduleDao scheduleDAO;
    private TherapistDao therapistDAO;

    @Override
    public void init() throws ServletException {
        scheduleDAO = new TherapistScheduleDao();
        therapistDAO = new TherapistDao();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        HttpSession session = request.getSession();

        try {
            switch (action) {
                case "add":
                    addSchedule(request, response);
                    break;
                case "update":
                    updateSchedule(request, response);
                    break;
                case "delete":
                    deleteSchedule(request, response);
                    break;
                default:
                    response.sendRedirect("schedules.jsp");
            }
        } catch (Exception e) {
            session.setAttribute("errorMessage", "Error: " + e.getMessage());
            response.sendRedirect("schedules.jsp");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        
       
    
        try {
            if (action == null) {
                listSchedules(request, response);
            } else if (action.equals("edit")) {
                showEditForm(request, response);
            } else if (action.equals("getTherapists")) {
                getTherapistsForDropdown(response);
            
            } 
            
            else if (action.equals("viewMySchedule")) {
   Object therapistIdObj = session.getAttribute("therapistId");
if (therapistIdObj == null) {
    response.sendRedirect("TherapistLogin.jsp");
    return;
}
int therapistId = (int) therapistIdObj;

    TherapistScheduleDao scheduleDao = new TherapistScheduleDao();
    List<TherapistSchedule> mySchedules = scheduleDao.getScheduleByTherapistId(therapistId);
    request.setAttribute("mySchedules", mySchedules);
   RequestDispatcher dispatcher = request.getRequestDispatcher("therapist-schedule-view.jsp");

   

   dispatcher.forward(request, response);
}

            else {
                listSchedules(request, response);
            }
        } catch (Exception e) {
            session.setAttribute("errorMessage", "Error: " + e.getMessage());
            response.sendRedirect("schedules.jsp");
        }
    }

    private void listSchedules(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        List<TherapistSchedule> schedules = scheduleDAO.getAllSchedules();
        request.setAttribute("schedules", schedules);
        request.getRequestDispatcher("schedules.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        int scheduleId = Integer.parseInt(request.getParameter("id"));
        TherapistSchedule schedule = scheduleDAO.getScheduleById(scheduleId);
        request.setAttribute("schedule", schedule);
        request.getRequestDispatcher("edit-schedule.jsp").forward(request, response);
    }

    private void addSchedule(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        int therapistId = Integer.parseInt(request.getParameter("therapistId"));
        String dayOfWeek = request.getParameter("dayOfWeek");
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        boolean isActive = Boolean.parseBoolean(request.getParameter("isActive"));

        TherapistSchedule newSchedule = new TherapistSchedule(therapistId, dayOfWeek, startTime, endTime, isActive);
        scheduleDAO.addSchedule(newSchedule);

        HttpSession session = request.getSession();
        session.setAttribute("successMessage", "Schedule added successfully!");
        response.sendRedirect("TherapistScheduleServlet");
    }

    private void updateSchedule(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        int scheduleId = Integer.parseInt(request.getParameter("scheduleId"));
        int therapistId = Integer.parseInt(request.getParameter("therapistId"));
        String dayOfWeek = request.getParameter("dayOfWeek");
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        boolean isActive = Boolean.parseBoolean(request.getParameter("isActive"));

        TherapistSchedule schedule = new TherapistSchedule(therapistId, dayOfWeek, startTime, endTime, isActive);
        schedule.setScheduleId(scheduleId);
        scheduleDAO.updateSchedule(schedule);

        HttpSession session = request.getSession();
        session.setAttribute("successMessage", "Schedule updated successfully!");
        response.sendRedirect("TherapistScheduleServlet");
    }

    private void deleteSchedule(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        int scheduleId = Integer.parseInt(request.getParameter("id"));
        scheduleDAO.deleteSchedule(scheduleId);

        HttpSession session = request.getSession();
        session.setAttribute("successMessage", "Schedule deleted successfully!");
        response.sendRedirect("TherapistScheduleServlet");
    }
    
    

    private void getTherapistsForDropdown(HttpServletResponse response) throws SQLException, IOException {
        List<Therapist> therapists = therapistDAO.getAllTherapists();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < therapists.size(); i++) {
            Therapist t = therapists.get(i);
            json.append("{")
                .append("\"therapist_ID\":").append(t.getTherapist_ID()).append(",")
                .append("\"therapist_fullname\":\"").append(t.getTherapist_fullname()).append("\",")
                .append("\"gender\":\"").append(t.getGender()).append("\"")
                .append("}");
            if (i < therapists.size() - 1) json.append(",");
        }
        json.append("]");

        try (PrintWriter out = response.getWriter()) {
            out.write(json.toString());
        }
    }
}
