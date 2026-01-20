package com.Utils;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailUtil {

    private static final Logger logger = Logger.getLogger(EmailUtil.class.getName());

    // Email config
    private static final String FROM_EMAIL = "ainabalqis17@gmail.com";
    private static final String FROM_NAME = "Alternative Therapy Centre Management System";
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String SMTP_USERNAME = FROM_EMAIL;
    private static final String SMTP_PASSWORD = "rmefbenlpigcooqq"; // App password
    private static final String VERIFY_BASE_URL = "http://localhost:8080/ATCMS3/verify-email.jsp";
    private static final String LOGIN_URL = "http://localhost:8080/ATCMS3/Login.jsp";

    // Thread pool for async email sending
    private static final ExecutorService emailExecutor = Executors.newFixedThreadPool(3);

    // ===============================
    // PUBLIC METHOD - SEND VERIFICATION EMAIL (Async)
    // ===============================
   public static void sendVerificationEmail(final String recipientEmail, final String fullname, final String token) {
        emailExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    String subject = "ATCMS - Email Verification";
                    String verifyLink = VERIFY_BASE_URL + "?token=" + token;

                    System.out.println("DEBUG: Sending verification to " + recipientEmail);
                    System.out.println("DEBUG: Verification link: " + verifyLink);

                    String body = buildVerificationEmail(fullname, verifyLink);
                    sendEmail(recipientEmail, subject, body);

                    System.out.println("Verification email sent successfully to " + recipientEmail);
                } catch (Exception e) {
                    System.err.println("ERROR sending email to " + recipientEmail);
                    e.printStackTrace();
                    logger.log(Level.SEVERE, "Failed to send verification email to: " + recipientEmail, e);
                }
            }
        });
    }

    // ===============================
    // BUILD HTML BODY
    // ===============================
    private static String buildVerificationEmail(String name, String link) {
        return "<!DOCTYPE html>"
                + "<html><head><style>"
                + "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; }"
                + ".header { color: #27445D; font-size: 24px; margin-bottom: 20px; }"
                + ".button { background-color: #27445D; color: white; padding: 10px 15px; text-decoration: none; border-radius: 5px; display: inline-block; }"
                + ".note { color: #666; font-size: 14px; margin-top: 20px; }"
                + "</style></head>"
                + "<body>"
                + "<div class='header'>ATCMS Email Verification</div>"
                + "<p>Dear " + name + ",</p>"
                + "<p>Thank you for registering with ATCMS. Please verify your email address by clicking the button below:</p>"
                + "<a href='" + link + "' class='button'>Verify Email Address</a>"
                + "<p class='note'>If you did not register with ATCMS, please ignore this email.</p>"
                + "<p>Best regards,<br>The ATCMS Team</p>"
                + "</body></html>";
    }

    // ===============================
    // SEND EMAIL CORE FUNCTION
    // ===============================
    private static void sendEmail(String toEmail, String subject, String htmlBody)
            throws MessagingException, UnsupportedEncodingException {

        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.debug", "true");
         props.put("mail.smtp.ssl.trust", SMTP_HOST); // Add trust to SMTP host
         
         

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_USERNAME, SMTP_PASSWORD);
            }
        });

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(FROM_EMAIL, FROM_NAME));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        msg.setSubject(subject);
        msg.setContent(htmlBody, "text/html; charset=utf-8");

        Transport.send(msg);
    }

    public static void shutdown() {
        emailExecutor.shutdown();
        try {
            if (!emailExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                logger.warning("Email executor did not terminate gracefully");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.log(Level.SEVERE, "Email executor shutdown interrupted", e);
        }
    }
    
    public static void sendResetPasswordEmail(final String recipientEmail, final String fullname, final String resetLink) {
    emailExecutor.submit(new Runnable() {  // <-- Guna anonymous class
        @Override
        public void run() {
            try {
                String subject = "ATCMS - Reset Your Password";
                String body = buildResetPasswordEmail(fullname, resetLink);
                sendEmail(recipientEmail, subject, body);
                System.out.println("✅ Reset password email sent to " + recipientEmail);
            } catch (Exception e) {
                System.err.println("❌ Failed to send reset email: " + e.getMessage());
            }
        }
    });

}

private static String buildResetPasswordEmail(String name, String link) {
    return "<!DOCTYPE html><html><head><style>"
            + "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; }"
            + ".button { background-color: #27445D; color: white; padding: 10px 15px; text-decoration: none; border-radius: 5px; }"
            + "</style></head>"
            + "<body>"
            + "<h2>Password Reset Request</h2>"
            + "<p>Hi " + name + ",</p>"
            + "<p>We received a request to reset your password. Click the button below to proceed:</p>"
            + "<a href='" + link + "' class='button'>Reset Password</a>"
            + "<p>If you didn't request this, please ignore this email.</p>"
            + "<p>Best regards,<br>ATCMS Team</p>"
            + "</body></html>";
}

public static void sendAppointmentReminderEmail(
    final String toEmail,
    final String fullname,
    final String date,
    final String time,
    final String packageName,
    final String therapistName
) {
    emailExecutor.submit(new Runnable() {
        @Override
        public void run() {
            try {
                String subject = "ATCMS - Appointment Reminder";

                String body = "<html><body style='font-family:Arial,sans-serif; max-width:600px; margin:0 auto;'>"
                        + "<h2 style='color:#007bff;'>Hi " + fullname + ",</h2>"
                        + "<p>This is a friendly reminder for your upcoming appointment tomorrow:</p>"
                        + "<div style='background-color:#f2f2f2; padding:15px; border-radius:8px;'>"
                        + "<p><strong>Date:</strong> " + date + "</p>"
                        + "<p><strong>Time:</strong> " + time + "</p>"
                        + "<p><strong>Package:</strong> " + packageName + "</p>"
                        + "<p><strong>Therapist:</strong> " + therapistName + "</p>"
                        + "</div>"
                        + "<p>Please arrive at least 10 minutes earlier. If you need to reschedule, kindly contact us in advance.</p>"
                        + "<p>Thank you and see you soon!</p>"
                        + "<p style='margin-top:30px;'>Regards,<br><strong>ATCMS Team</strong></p>"
                        + "</body></html>";

                sendEmail(toEmail, subject, body);
                logger.info("Reminder email sent to: " + toEmail);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failed to send reminder email to: " + toEmail, e);
            }
        }
    });
}
public static void sendPendingEmail(
        final String toEmail,
        final String fullname,
        final String date,
        final String time,
        final String packageName,
        final String therapistName) {

    emailExecutor.submit(new Runnable() {
        @Override
        public void run() {
            try {
                String subject = "ATCMS - Appointment Pending Confirmation";

                String body = "<html><body style='font-family:Arial,sans-serif;'>"
                        + "<h2>Hi " + fullname + ",</h2>"
                        + "<p>Thank you for booking an appointment with us. Your appointment is currently <strong>Pending</strong> and will be confirmed after payment.</p>"
                        + "<h3>Appointment Details:</h3>"
                        + "<ul>"
                        + "<li><strong>Date:</strong> " + date + "</li>"
                        + "<li><strong>Time:</strong> " + time + "</li>"
                        + "<li><strong>Package:</strong> " + packageName + "</li>"
                        + "<li><strong>Therapist:</strong> " + therapistName + "</li>"
                        + "</ul>"
                        + "<p>Please proceed with the payment to confirm your booking.</p>"
                        + "<p>Thank you and we look forward to seeing you soon!</p>"
                        + "<p>Regards,<br>ATCMS Team</p>"
                        + "</body></html>";

                sendEmail(toEmail, subject, body);
                logger.info("Pending email sent to: " + toEmail);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failed to send pending email to: " + toEmail, e);
            }
        }
    });
}


public static void sendConfirmedEmail(String toEmail, String fullname) {
    String subject = "ATCMS - Appointment Confirmed";
    String body = "<html><body>"
            + "<h2>Hi " + fullname + ",</h2>"
            + "<p>Your appointment has been successfully confirmed. We look forward to seeing you soon!</p>"
            + "<p>Regards,<br>ATCMS Team</p>"
            + "</body></html>";
    sendAsyncEmail(toEmail, subject, body);
}



public static void sendCancelledEmail(String toEmail, String fullname) {
    String subject = "ATCMS - Appointment Cancelled";
    String body = "<html><body>"
            + "<h2>Hi " + fullname + ",</h2>"
            + "<p>Your appointment has been cancelled as requested.</p>"
            + "<p>If you have any questions, please contact us.</p>"
            + "<p>Regards,<br>ATCMS Team</p>"
            + "</body></html>";
    sendAsyncEmail(toEmail, subject, body);
}

public static void sendRefundRequestedEmail(String toEmail, String fullname) {
    String subject = "ATCMS - Refund Requested";
    String body = "<html><body>"
            + "<h2>Hi " + fullname + ",</h2>"
            + "<p>We have received your refund request. We will process it as soon as possible.</p>"
            + "<p>Regards,<br>ATCMS Team</p>"
            + "</body></html>";
    sendAsyncEmail(toEmail, subject, body);
}

public static void sendRefundCompletedEmail(String toEmail, String fullname) {
    String subject = "ATCMS - Refund Completed";
    String body = "<html><body>"
            + "<h2>Hi " + fullname + ",</h2>"
            + "<p>Your refund has been successfully processed. Thank you for your patience.</p>"
            + "<p>Regards,<br>ATCMS Team</p>"
            + "</body></html>";
    sendAsyncEmail(toEmail, subject, body);
}

public static void sendRescheduleEmail(final String toEmail, final String fullname, final String newDate, final String newTime) {
    emailExecutor.submit(new Runnable() {
        @Override
        public void run() {
            try {
                String subject = "ATCMS - Appointment Rescheduled";
                String body = "<html><body style='font-family:Arial,sans-serif;'>"
                        + "<h2>Hi " + fullname + ",</h2>"
                        + "<p>Your appointment has been successfully rescheduled. Here are your new details:</p>"
                        + "<ul>"
                        + "<li><strong>Date:</strong> " + newDate + "</li>"
                        + "<li><strong>Time:</strong> " + newTime + "</li>"
                        + "</ul>"
                        + "<p>Please contact us if you have any questions or need further assistance.</p>"
                        + "<p>Regards,<br>ATCMS Team</p>"
                        + "</body></html>";

                sendEmail(toEmail, subject, body);
                logger.info("Reschedule email sent to: " + toEmail);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failed to send reschedule email to: " + toEmail, e);
            }
        }
    });
}
public static void sendTherapistRequestRescheduleEmail(final String toEmail, final String fullname, final String reason) {
    emailExecutor.submit(new Runnable() {
        @Override
        public void run() {
            try {
                String subject = "ATCMS - Request to Reschedule Your Appointment";
                String body = "<html><body style='font-family:Arial,sans-serif;'>"
                        + "<h2>Hi " + fullname + ",</h2>"
                        + "<p>Your therapist has requested to reschedule your appointment for the following reason:</p>"
                        + "<blockquote style='background:#f9f9f9;padding:10px;border-left:5px solid #ccc;'>"
                        + reason + "</blockquote>"
                        + "<p>Please log in to your ATCMS account to approve or reject this request.</p>"
                        + "<p>Thank you for your understanding.</p>"
                        + "<p>Regards,<br>ATCMS Team</p>"
                        + "</body></html>";

                sendEmail(toEmail, subject, body);
                logger.info("Therapist reschedule request email sent to: " + toEmail);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failed to send therapist reschedule request email to: " + toEmail, e);
            }
        }
    });
}



public static void sendCompletedEmail(String toEmail, String fullname) {
    String subject = "ATCMS - Thank You for Your Visit";
    String body = "<html><body style='font-family: Arial, sans-serif; line-height: 1.6;'>"
            + "<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>"
            + "<h2 style='color: #2c3e50;'>Dear " + fullname + ",</h2>"
            + "<p>Thank you for attending your recent appointment with us. We truly appreciate you choosing our services.</p>"
            + "<p>Your feedback is invaluable to us as we strive to continuously improve our services. "
            + "We would be grateful if you could take a moment to share your experience with us. Kindly submit it on the ATCMS portal.</p>"
            + "<p>It was our pleasure to serve you, and we look forward to the opportunity to assist you again in the future.</p>"
            + "<p style='margin-top: 30px;'>Warm regards,<br>"
            + "<strong>The ATCMS Team</strong></p>"
            + "<p style='font-size: 12px; color: #7f8c8d; margin-top: 20px;'>"
            + "This is an automated message - please do not reply directly to this email.</p>"
            + "</div></body></html>";
    sendAsyncEmail(toEmail, subject, body);
}

public static void sendRefundCompletedEmail(final String toEmail, final String fullname, final String method, final String notes) {
    emailExecutor.submit(new Runnable() {
        @Override
        public void run() {
            try {
                String subject = "ATCMS - Refund Completed";
                String body = "<html><body style='font-family:Arial,sans-serif;'>"
                        + "<h2>Hi " + fullname + ",</h2>"
                        + "<p>We have successfully processed your refund.</p>"
                        + "<ul>"
                        + "<li><strong>Refund Method:</strong> " + method + "</li>"
                        + "<li><strong>Notes:</strong> " + notes + "</li>"
                        + "</ul>"
                        + "<p>Please allow a few business days for the refund to appear in your account, depending on your bank or payment provider.</p>"
                        + "<p>Thank you for your patience.</p>"
                        + "<p>Regards,<br>ATCMS Team</p>"
                        + "</body></html>";

                sendEmail(toEmail, subject, body);
                logger.info("Refund completed email sent to: " + toEmail);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failed to send refund email to: " + toEmail, e);
            }
        }
    });
}


// ---------- Helper method untuk hantar async ----------
private static void sendAsyncEmail(final String toEmail, final String subject, final String htmlBody) {
    emailExecutor.submit(new Runnable() {
        @Override
        public void run() {
            try {
                sendEmail(toEmail, subject, htmlBody);
                logger.info("Email sent to: " + toEmail + " [" + subject + "]");
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failed to send email to: " + toEmail, e);
            }
        }
    });
}


}