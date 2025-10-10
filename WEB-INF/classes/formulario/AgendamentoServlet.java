Back-end:
package formulario;

import java.io.IOException;
import java.util.Properties;

// Imports JavaMail/Jakarta Mail
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication; 
import javax.mail.Session; 
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

// Imports Servlet
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/AgendamentoServlet")
public class AgendamentoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Coleta dos dados do formulário
        String nome = request.getParameter("nome");
        String email = request.getParameter("email");
        String telefone = request.getParameter("telefone");

        String remetente = "studiomocotatoo@gmail.com";
        String senhaApp = "tlevqrnodyxbrlks"; // SENHA DE APP
        String destinatarioEstudio = "studiomocotatoo@gmail.com";

        // 2. Configurações SMTP para GMAIL
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // 3. Criação da Sessão com Autenticação
        Session session = Session.getInstance(props, new Authenticator() {
            @Override    
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(remetente, senhaApp);
            }
        });

        try {
            // 4. Enviar e-mail para o Estúdio
            Message emailEstudio = new MimeMessage(session);
            emailEstudio.setFrom(new InternetAddress(remetente));
            emailEstudio.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatarioEstudio));
            emailEstudio.setSubject("Novo agendamento - " + nome);
            emailEstudio.setText("Nome: " + nome + "\nEmail: " + email + "\nTelefone: " + telefone);
            Transport.send(emailEstudio);

            // 5. Enviar e-mail de Confirmação para o Cliente
            Message emailCliente = new MimeMessage(session);
            emailCliente.setFrom(new InternetAddress(remetente));
            emailCliente.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            emailCliente.setSubject("Confirmação de Agendamento");
            emailCliente.setText(
                    "Olá " + nome + ",\n\n" +
                    "Obrigado por agendar um horário conosco no Studio Mocotattoo!\n\n" +
                    "Aqui estão os detalhes que recebemos:\n" +
                    "- Nome: " + nome + "\n" +
                    "- E-mail: " + email + "\n" +
                    "- Telefone: " + telefone + "\n\n" +
                    "Nossa equipe entrará em contato em breve para confirmar o horário do seu atendimento.\n\n" +
                    "Se precisar alterar algum dado ou cancelar o agendamento, entre em contato com nosso e-mail: studiomocotatoo@gmail.com ou pelo telefone: (XX) XXXXX-XXXX.\n\n" +
                    "Aguardamos você!\n\n" +
                    "Atenciosamente,\n" +
                    "Equipe Studio Mocotattoo"
            );

            Transport.send(emailCliente);

            // 6. Resposta: mostrar mensagem de sucesso
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write("<h2>Agendamento enviado com sucesso ✅</h2>");

        } catch (MessagingException e) {
            e.printStackTrace();

            // 7. Resposta: mostrar mensagem de erro
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write("<h2>Erro no envio ❌</h2>");
        }
    }
}
