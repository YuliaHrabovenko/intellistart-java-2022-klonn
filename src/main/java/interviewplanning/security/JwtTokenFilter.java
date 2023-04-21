package interviewplanning.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import interviewplanning.exceptions.AbstractCommonException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT Filter for token check.
 */
public class JwtTokenFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;

  public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {

    String token = jwtTokenProvider.resolveToken(request);

    try {
      if (token != null && jwtTokenProvider.validateToken(token)) {
        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        if (authentication != null) {
          SecurityContextHolder.getContext().setAuthentication(authentication);
        }
      }
    } catch (AbstractCommonException commonEx) {
      response.getWriter().print(new ObjectMapper().writeValueAsString(commonEx));
      response.setContentType("application/json");
      response.setStatus(commonEx.getStatusCode());
      return;
    }

    filterChain.doFilter(request, response);
  }
}
