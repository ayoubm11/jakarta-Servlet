package country.web;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter(urlPatterns = {"/addCountry.html", "/addCountry"})
public class AuthenticationFilter implements Filter {	
	@Override
    public void init(FilterConfig filterConfig) throws ServletException {
		System.out.println("AuthenticationFilter initialisé");
	}
	
	@Override
	public void doFilter (ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        HttpSession session = httpRequest.getSession(false);
        
        boolean isLoggedIn = (session != null && session.getAttribute("user") != null);
        if(isLoggedIn) {
        	chain.doFilter(request, response);
        }else{
            httpResponse.sendRedirect("login.html?error=notAuthenticated");
        }
	}
	
	@Override
    public void destroy() {
        System.out.println("AuthenticationFilter détruit");
	}
}















