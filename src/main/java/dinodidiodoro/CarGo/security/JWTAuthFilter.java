package dinodidiodoro.CarGo.security;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import dinodidiodoro.CarGo.exceptions.UnauthorizedException;
import dinodidiodoro.CarGo.user.User;
import dinodidiodoro.CarGo.user.UsersService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTAuthFilter extends OncePerRequestFilter {

	@Autowired
	JWTTools jwttools;
	@Autowired
	UsersService userService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
	        throws ServletException, java.io.IOException {
	    if (shouldNotFilter(request)) {
	        filterChain.doFilter(request, response);
	        return;
	    }
	    
	    String authHeader = request.getHeader("Authorization");
	    
	    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
	        throw new UnauthorizedException("Per favore passa il token nell'authorization header");
	    }
	    
	    String token = authHeader.substring(7);
	    jwttools.verifyToken(token);
	    String id = jwttools.extractSubject(token);
	    User currentUser = userService.findById(UUID.fromString(id));

	    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(currentUser, null,
	            currentUser.getAuthorities());
	    SecurityContextHolder.getContext().setAuthentication(authToken);
	    filterChain.doFilter(request, response);
	}



	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
	    AntPathMatcher pathMatcher = new AntPathMatcher();
	    String path = request.getServletPath();
	    String method = request.getMethod();
	    
	    return pathMatcher.match("/auth/**", path) 
	        || pathMatcher.match("/cars", path) 
	        || (pathMatcher.match("/cars/**", path) && "GET".equalsIgnoreCase(method));
	}



}