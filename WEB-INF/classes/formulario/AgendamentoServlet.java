package formulario;

import java.io.IOException;
import java.util.Properties;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Transport;
import jakarta.mail.Session;
import jakarta.mail.Authenticator;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/AgendamentoServlet")
public class AgendamentoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    // A URL base do seu frontend no GitHub Pages
    private static final String FRONTEND_ORIGIN = "https://fabiu-ferreira.github.io/mocotattoo-studio";

    // Método para lidar com requisições OPTIONS (CORS)
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // CORREÇÃO CRÍTICA: Define a origem exata do seu frontend
        response.setHeader("Access-Control-Allow-Origin", FRONTEND_ORIGIN);
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Headers CORS para o POST
        response.setHeader("Access-Control-Allow-Origin", FRONTEND_ORIGIN);
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        // 1️⃣ Coleta dos dados do formulário
        String nome = request.getParameter("nome");
        String email = request.getParameter("email");
        String telefone = request.getParameter("telefone");

        String remetente = "studiomocotatoo@gmail.com";
        String senhaApp = System.getenv("Senha_Gmail"); // SENHA DE APP

        // 2️⃣ Verifica se a variável de ambiente está correta
        System.out.println("Senha lida do ambiente: " + senhaApp);
        if (senhaApp == null || senhaApp.isEmpty()) {
            // Em caso de erro na variável de ambiente, loga e redireciona para erro
            System.err.println("Erro: Variável de ambiente Senha_Gmail não encontrada ou vazia!");
            response.sendRedirect(FRONTEND_ORIGIN + "/?status=config_error");
            return;
        }

        String destinatarioEstudio = "studiomocotatoo@gmail.com";

        // 3️⃣ Configurações SMTP
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // 4️⃣ Criação da Sessão SMTP
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(remetente, senhaApp);
            }
        });

        try {
            // Enviar e-mail para o Estúdio
            Message emailEstudio = new MimeMessage(session);
            emailEstudio.setFrom(new InternetAddress(remetente));
            emailEstudio.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatarioEstudio));
            emailEstudio.setSubject("Novo agendamento - " + nome);
            emailEstudio.setText("Nome: " + nome + "\nEmail: " + email + "\nTelefone: " + telefone);
            Transport.send(emailEstudio);

            // Enviar e-mail de Confirmação para o Cliente
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

            // 6. Resposta: REDIRECIONAMENTO para a página inicial com status de sucesso
            response.sendRedirect(FRONTEND_ORIGIN + "/?status=success");
            
        } catch (MessagingException e) {
            e.printStackTrace();

            // 7. Resposta: REDIRECIONAMENTO para a página inicial com status de erro
            response.sendRedirect(FRONTEND_ORIGIN + "/?status=error");
        }
    }
}
