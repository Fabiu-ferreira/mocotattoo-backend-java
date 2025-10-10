<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Agendamento - Studio Mocotattoo</title>

<body>
    <h1>Agende seu atendimento</h1>
    
<form id="formAgendamento" action="https://mocotattoo-backend-java-1.onrender.com" method="POST">
    <input type="text" name="nome" placeholder="Seu nome" required><br>
    <input type="email" name="email" placeholder="Seu e-mail" required><br>
    <input type="tel" name="telefone" placeholder="Seu telefone" required><br>
    <button type="submit">Enviar</button>
</form>
</body>

</html>
