
package formulario;

import java.io.IOException;
import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/AgendamentoServlet")
public class AgendamentoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "https://fabiu-ferreira.github.io");
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // CORS
        response.setHeader("Access-Control-Allow-Origin", "https://fabiu-ferreira.github.io");
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        // 1️⃣ Coleta dos dados do formulário
        String nome = request.getParameter("nome");
        String email = request.getParameter("email");
        String telefone = request.getParameter("telefone");

        String remetente = "studiomocotatoo@gmail.com";
        String senhaApp = System.getenv("Senha_Gmail"); // SENHA DE APP
        String destinatarioEstudio = "studiomocotatoo@gmail.com";

        // 2️⃣ Verifica variável de ambiente
        if (senhaApp == null || senhaApp.isEmpty()) {
            throw new ServletException("Variável de ambiente Senha_Gmail não encontrada!");
        }

        // 3️⃣ Configurações SMTP
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(remetente, senhaApp);
            }
        });

        // 4️⃣ Resposta imediata para o usuário
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(
            "<div style='position:relative; width:100%; height:100vh;'>" +
            "<img src='imagens/Imagem-sucesso.png' alt='Sucesso' style='width:100%; height:100%; object-fit:cover;' />" +
            "<h2 style='position:absolute; top:2%; left:50%; transform:translate(-50%, -50%); color:white; font-size:48px; text-shadow:2px 2px 4px #000;'>Agendamento recebido ✅</h2>" +
            "</div>"
        );

        // 5️⃣ Envio de e-mails em thread separada
        new Thread(() -> {
            try {
                // E-mail para estúdio
                Message emailEstudio = new MimeMessage(session);
                emailEstudio.setFrom(new InternetAddress(remetente));
                emailEstudio.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatarioEstudio));
                emailEstudio.setSubject("Novo agendamento - " + nome);
                emailEstudio.setText("Nome: " + nome + "\nEmail: " + email + "\nTelefone: " + telefone);
                Transport.send(emailEstudio);

                // E-mail para cliente
                Message emailCliente = new MimeMessage(session);
                emailCliente.setFrom(new InternetAddress(remetente));
                emailCliente.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
                emailCliente.setSubject("Confirmação de Agendamento");
                emailCliente.setText(
                    "Olá " + nome + ",\n\n" +
                    "Obrigado por agendar um horário conosco no Studio Mocotattoo!\n\n" +
                    "Detalhes:\n" +
                    "- Nome: " + nome + "\n" +
                    "- E-mail: " + email + "\n" +
                    "- Telefone: " + telefone + "\n\n" +
                    "Nossa equipe entrará em contato em breve.\n\n" +
                    "Atenciosamente,\nEquipe Studio Mocotattoo"
                );
                Transport.send(emailCliente);

            } catch (MessagingException e) {
                e.printStackTrace(); // Apenas logs, não bloqueia o usuário
            }
        }).start();
    }
}
