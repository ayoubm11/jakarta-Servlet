
package country.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import country.dao.User;

import java.io.IOException;

@WenServlet("/login")
public class LoginServlet extends HttpServlet{
	
	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		
		User user  = User.authenticate(email, password);
		
		if(user != null) {
			HttpSession session = request.getSession(true);
			
			session.setAttribute("user", user);
			session.setAttribute("userEmail", user.getEmail());
			
			session.setMaxInactiveInterval(1800);
			
			response.sendRedirect("countries");
		}else {
            request.setAttribute("errorMessage", "Email ou mot de passe incorrect");
            request.getRequestDispatcher("/login.html").forward(request, response);
		}		
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
		response.sendRedirect("login.html");
	}
}














